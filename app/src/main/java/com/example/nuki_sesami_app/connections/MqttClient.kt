package com.example.nuki_sesami_app.connections

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttClient(
    hostname: String,
    port: Int,
    private var username: String,
    private var passwd: String,
    private val nukiDeviceID: String,
): NukiSesamiConnection() {
    /** Contains the actual PAHO client handle */
    private var mqtt: MqttAsyncClient = MqttAsyncClient(
        "tcp://$hostname:$port",
        MqttAsyncClient.generateClientId(),
        MemoryPersistence()
    )

    init {
        mqtt.setCallback(object : MqttCallback{
            override fun connectionLost(cause: Throwable?) {
                Log.w("mqtt", "connectionLost: ${cause.toString()}")
                connected.value = false
                error.value = "connectionLost: ${cause.toString()}"
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (topic != null && message != null) {
                    val msg = message.payload.decodeToString()
                    Log.d("mqtt", "message(${topic}): $msg")
                    notify(topic, msg)
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // no action
            }
        })

        val options = MqttConnectOptions().apply {
            isCleanSession = false
            keepAliveInterval = 60
            if (username.isNotEmpty() && passwd.isNotEmpty()) {
                password = passwd.toCharArray()
                userName = username
            }
        }

        mqtt.connect(options, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.i("mqtt", "connected(this=$this)")
                connected.value = true
                error.value = ""
                mqtt.subscribe("nuki/${nukiDeviceID}/state", 0)
                mqtt.subscribe("nuki/${nukiDeviceID}/doorsensorState", 0)
                mqtt.subscribe("sesami/${nukiDeviceID}/state", 0)
                mqtt.subscribe("sesami/${nukiDeviceID}/mode", 0)
                mqtt.subscribe("sesami/${nukiDeviceID}/version", 0)
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.w("mqtt", "connect failed: $exception")
                connected.value = false
                error.value = "onFailure: $exception"
            }
        })
    }

    override fun close() {
        Log.d("mqtt", "close(this=$this)")
        if (mqtt.isConnected) {
            mqtt.disconnect()
        }

        mqtt.close()
    }

    /** Publishes a message on a topic */
    override fun publish(topic: String, value: String) {
        val message = MqttMessage()
        message.payload = value.toByteArray()
        message.qos = 0
        mqtt.publish(topic, message)
    }
}
