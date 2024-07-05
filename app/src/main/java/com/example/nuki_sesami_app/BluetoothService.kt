package com.example.nuki_sesami_app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.nuki_sesami_app.base.ObservableState
import com.example.nuki_sesami_app.errors.BluetoothServiceError
import com.example.nuki_sesami_app.jsonrpc.StatusMessage
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method
import java.util.Timer
import kotlin.concurrent.timer

class BluetoothService(
    val context: Context,
    private val nukiDeviceID: String,
    private var address: String = "",
    private var name: String = "",
    val channel: Int = 4,
) {
    /** Send and receive RCFCOMM data on this socket */
    private var socket: BluetoothSocket

    /** Observable state, will be set to true when connected */
    var connected = ObservableState(false)

    /** Observable state, will be set in case of (connection) errors */
    var error = ObservableState("")

    /** List of subscribers to message events */
    private val observers = ArrayList<(String, String) -> Unit>()

    /** background reader and connection check */
    private var timer: Timer

    /** contains to be processed received data */
    private var received: String = ""

    init {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED) {
            throw BluetoothServiceError("Got no Bluetooth connect permission")
        }

        val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            ?: throw BluetoothServiceError("Failed to obtain Bluetooth adapter")

        adapter.cancelDiscovery()
        val devices: Set<BluetoothDevice>? = adapter.bondedDevices
        val device: BluetoothDevice = devices?.firstOrNull { it.address == address || it.name == name }
            ?: throw BluetoothServiceError("Bluetooth device(name=$name, address=$address) not found")

        name = device.name
        address = device.address

        val m: Method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
        socket = m.invoke(device, channel) as BluetoothSocket

        try {
            socket.connect()
        } catch(e: IOException) {
            throw BluetoothServiceError("Bluetooth socket.connect failed", e)
        }

        error.value = ""
        Log.d("bluetooth", "connected($name, addr=$address, channel=$channel)")

        this.timer = timer(
            name = "NukiSesami.BluetoothService",
            daemon = false,
            initialDelay = 1000,
            period = 1000,
            action = {
                connected.value = socket.isConnected
                Log.d("bluetooth", "tick(connected=${connected.value})")

                try {
                    receive(socket.inputStream)
                } catch(e: IOException) {
                    connected.value = false
                    error.value = e.toString()
                }
            }
        )
    }

    private fun receive(input: InputStream) {
        var remaining = input.available()
        Log.d("bluetooth", "receive(remaining=$remaining)")

        while (remaining >= 1) {
            val char: Char = input.read().toChar()

            if (char == '\n') {
                received = process(received)
                break
            }

            received += char
            remaining = input.available()
        }
    }

    private fun processStatusMessage(msg: StatusMessage) {
        notify("sesami/${nukiDeviceID}/version", msg.params.version)
        notify("sesami/${nukiDeviceID}/state", msg.params.door.state)
        notify("sesami/${nukiDeviceID}/mode", msg.params.door.mode)
        notify("nuki/${nukiDeviceID}/doorsensorState", msg.params.nuki.doorsensor)
        notify("nuki/${nukiDeviceID}/state", msg.params.nuki.lock)
        notify("sesami/${nukiDeviceID}/relay/opendoor", if(msg.params.relay.opendoor) 1 else 0)
        notify("sesami/${nukiDeviceID}/relay/openhold", if(msg.params.relay.openhold) 1 else 0)
        notify("sesami/${nukiDeviceID}/relay/openclose", if(msg.params.relay.openclose) 1 else 0)
    }

    private fun process(data: String): String {
        Log.d("bluetooth", "processing($data)")
        val s = data.replace("\n", "") // strip newlines
        val msg: StatusMessage

        try {
            msg = Gson().fromJson(s, StatusMessage::class.java)
            processStatusMessage(msg)
        } catch(e: JsonSyntaxException) {
            Log.e("bluetooth", "failed to process status message", e)
        }

        error.value = data
        return ""
    }

    fun close() {
        timer.cancel()
        timer.purge()
        socket.close()
        connected.value = false
        error.value = ""
        Log.d("bluetooth", "closed")
    }

    /** Notifies all observers a new message for a specific topic has arrived */
    private fun notify(topic: String, message: String) {
        observers.forEach {
            it(topic, message)
        }
    }

    private fun notify(topic: String, value: Int) {
        notify(topic, value.toString())
    }

    /** Used by observers so they can be notified when a message has arrived */
    fun subscribe(observer: (String, String) -> Unit) {
        observers.add(observer)
    }

    /** Publishes a message on the channel */
    fun publish(topic: String, value: String) {
        if (topic != "sesami/${nukiDeviceID}/request/state") {
            return
        }

        val msg = """{
            "jsonrpc": "2.0", +
            "method": "set", 
            "params": { "door_request_state": $value } 
        }""".trimIndent()

        Log.d("bluetooth", "send($msg)")

        try {
            socket.outputStream.write(msg.toByteArray())
            Log.d("bluetooth", "published($topic, $value)")
        } catch (e: IOException) {
            Log.e("bluetooth", "publish($topic, $value) failed", e)
            connected.value = false
            error.value = "publish($topic, $value) failed: $e"
        }
    }
}
