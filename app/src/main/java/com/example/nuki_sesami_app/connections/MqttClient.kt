package com.example.nuki_sesami_app.connections

import android.util.Log
import org.eclipse.paho.mqttv5.client.IMqttToken
import org.eclipse.paho.mqttv5.client.MqttActionListener
import org.eclipse.paho.mqttv5.client.MqttAsyncClient
import org.eclipse.paho.mqttv5.client.MqttCallback
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties


class MqttClient(
    private val hostname: String,
    private val port: Int,
    private val username: String,
    passwd: String,
    private val nukiDeviceID: String,
): NukiSesamiConnection() {
    private val clientID = "nuki-sesami-app"

    /** Contains the actual PAHO client handle */
    private var client: MqttAsyncClient = MqttAsyncClient(
        "tcp://$hostname:$port",
        clientID,
        MemoryPersistence()
    )

    init {
        client.setCallback(object: MqttCallback {
            override fun disconnected(disconnectResponse: MqttDisconnectResponse?) {
                Log.w("mqtt", "disconnected: ${disconnectResponse.toString()}")
                connected.value = false
                error.value = "disconnected: ${disconnectResponse.toString()}"
            }

            override fun mqttErrorOccurred(exception: MqttException?) {
                Log.w("mqtt", "mqttErrorOccurred: ${exception.toString()}")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic != null && message != null) {
                    val msg = message.payload.decodeToString()
                    Log.d("mqtt", "message(${topic}): $msg")
                    notify(topic, msg)
                }
            }

            override fun deliveryComplete(token: IMqttToken?) {
                Log.d("mqtt", "deliveryComplete: ${token.toString()}")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.i("mqtt", "connectComplete(reconnect=$reconnect, serverURI=$serverURI)")
                connected.value = true
                error.value = ""
                client.subscribe("nuki/${nukiDeviceID}/state", 0)
                client.subscribe("nuki/${nukiDeviceID}/doorsensorState", 0)
                client.subscribe("sesami/${nukiDeviceID}/state", 0)
                client.subscribe("sesami/${nukiDeviceID}/mode", 0)
                client.subscribe("sesami/${nukiDeviceID}/version", 0)
            }

            override fun authPacketArrived(reasonCode: Int, properties: MqttProperties?) {
                Log.i("mqtt", "authPacketArrived(reasonCode=$reasonCode, properties=$properties)")
            }
        })

        // ConnectOption is used to specify username and password
        val options = MqttConnectionOptions()
        options.keepAliveInterval = 60
        options.userName = username
        options.password = passwd.toByteArray()

        Log.i("mqtt", "connect(${client.clientId}, ${client.serverURI}), $options")

        try {
            client.connect(options, null, object : MqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i("mqtt", "connected($username@$hostname:$port)")
                    connected.value = true
                    error.value = ""
                    client.subscribe("nuki/${nukiDeviceID}/state", 0)
                    client.subscribe("nuki/${nukiDeviceID}/doorsensorState", 0)
                    client.subscribe("sesami/${nukiDeviceID}/state", 0)
                    client.subscribe("sesami/${nukiDeviceID}/mode", 0)
                    client.subscribe("sesami/${nukiDeviceID}/version", 0)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("mqtt", "connect failed: $exception")
                    connected.value = false
                    error.value = "onFailure: $exception"
                }
            })
        } catch (e: MqttException) {
            // Get stack trace
            e.printStackTrace()
        }
    }

    override fun close() {
        Log.d("mqtt", "close($username@$hostname:$port)")
        if (client.isConnected) {
            client.disconnect()
        }

        client.close()
    }

    /** Publishes a message on a topic */
    override fun publish(topic: String, value: String) {
        val message = MqttMessage()
        message.payload = value.toByteArray()
        message.qos = 0
        client.publish(topic, message)
    }
}
