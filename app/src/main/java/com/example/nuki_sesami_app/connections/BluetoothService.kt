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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method

class BluetoothService(
    val context: Context,
    val adapter: BluetoothAdapter?,
    val coroutineScope: CoroutineScope?,
    private val nukiDeviceID: String,
    private var address: String = "",
    private var name: String = "",
    val channel: Int = 4,
): NukiSesamiConnection() {
    /** Send and receive RCFCOMM data on this socket */
    private var socket = getBluetoothSocket()

    /** will be set to true when connection has been closed */
    private var closed = false

    /** contains to be processed received data */
    private var received: String = ""

    /** routine for connecting the socket */
    private var connector: Job? = coroutineScope?.launch {
        withContext(Dispatchers.IO) {
            try {
                Log.d("bluetooth", "connect($name, $address, $channel)")
                socket.connect()
                Log.d("bluetooth", "connected($name, $address, $channel)")
            } catch(e: IOException) {
                if (!closed) {
                    connected.value = false
                    error.value = e.toString()
                    Log.e("bluetooth", "connect($name, $address, $channel) failed", e)
                }
            } catch(e: SecurityException) {
                connected.value = false
                error.value = e.toString()
                Log.e("bluetooth", "connect($name, $address, $channel) failed", e)
            }
        }
    }

    /** routine for checking the connection state and receiving data from the socket */
    private var receiver: Job? = coroutineScope?.launch {
        withContext(Dispatchers.IO) {
            var connectAttempts = 0
            while (true) {
                delay(1000)
                connected.value = socket.isConnected

                if (socket.isConnected) {
                    try {
                        receive(socket.inputStream)
                    } catch (e: IOException) {
                        connected.value = false
                        error.value = e.toString()
                    }
                } else {
                    connectAttempts += 1

                    if (connectAttempts == MAX_CONNECT_ATTEMPTS) {
                        error.value = "Connect timeout ${MAX_CONNECT_ATTEMPTS}[s]"
                        Log.d("bluetooth", "receiver connect timeout ${MAX_CONNECT_ATTEMPTS}[s]")
                    }
                }
            }
        }
    }

    companion object {
        const val MAX_CONNECT_ATTEMPTS = 10
    }

    private fun getBluetoothSocket(): BluetoothSocket {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED) {
            throw BluetoothServiceError("got no connect permission")
        }

        if (adapter == null) {
            throw BluetoothServiceError("got no bluetooth adapter")
        }

        adapter.cancelDiscovery()
        val devices: Set<BluetoothDevice>? = adapter.bondedDevices
        val device: BluetoothDevice = devices?.firstOrNull { it.address == address || it.name == name }
            ?: throw BluetoothServiceError("device(name=$name, address=$address) not found")

        name = device.name
        address = device.address

        val m: Method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
        return m.invoke(device, channel) as BluetoothSocket
    }

    /** Store received data and process message when a newline is received */
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

    /** Inform subscribers of a received status message */
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

    /** Process a single message; at present only status messages are supported */
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
        closed = true
        connector?.cancel()
        receiver?.cancel()
        socket.close()
        connected.value = false
        error.value = ""
        Log.d("bluetooth", "closed")
    }

    override fun publish(topic: String, value: String) {
        if (!connected.value) {
            return
        }

        if (topic != "sesami/${nukiDeviceID}/request/state") { // the only topic supported
            return
        }

        try {
            socket.let { s ->
                val request = DoorRequestMessage(params=DoorRequestParams(state=value.toInt()))
                val msg = Gson().toJson(request) + "\n"
                s.outputStream.write(msg.toByteArray())
                Log.d("bluetooth", "published($topic, $value)")
            }
        } catch (e: IOException) {
            Log.e("bluetooth", "publish($topic, $value) failed", e)
            connected.value = false
            error.value = "publish($topic, $value) failed: $e"
        }
    }
}
