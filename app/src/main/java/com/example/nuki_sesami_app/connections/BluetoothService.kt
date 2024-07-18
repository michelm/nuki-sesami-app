package com.example.nuki_sesami_app.connections

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.nuki_sesami_app.errors.BluetoothServiceError
import com.example.nuki_sesami_app.jsonrpc.DoorRequestMessage
import com.example.nuki_sesami_app.jsonrpc.DoorRequestParams
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
    val adapter: BluetoothAdapter,
    private val nukiDeviceID: String,
    private var address: String = "",
    private var name: String = "",
    val channel: Int = 4,
): NukiSesamiConnection() {
    /** Send and receive RCFCOMM data on this socket */
    private var socket: BluetoothSocket

    /** background reader and connection check */
    private var timer: Timer

    /** contains to be processed received data */
    private var received: String = ""

    init {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED) {
            throw BluetoothServiceError("Got no Bluetooth connect permission")
        }
        
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

        return ""
    }

    override fun close() {
        timer.cancel()
        timer.purge()
        socket.close()
        connected.value = false
        error.value = ""
        Log.d("bluetooth", "closed")
    }

    /** Publishes a message on the channel */
    override fun publish(topic: String, value: String) {
        if (topic != "sesami/${nukiDeviceID}/request/state") {
            return
        }

        val request = DoorRequestMessage(params=DoorRequestParams(state=value.toInt()))
        val msg = Gson().toJson(request) + "\n"

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
