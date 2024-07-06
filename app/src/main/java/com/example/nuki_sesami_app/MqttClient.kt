package com.example.nuki_sesami_app

import android.util.Log
import com.example.nuki_sesami_app.base.ObservableState
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
) {
    /** Observable state, will be set to true when connected */
    var connected = ObservableState(false)

    /** Observable state, will be set in case of (connection) errors */
    var error = ObservableState("")

    /** List of subscribers to message events */
    private val observers = ArrayList<(String, String) -> Unit>()

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
                // TODO: throw exception?
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
                // TODO: throw exception?
            }
        })
    }

    fun close() {
        Log.d("mqtt", "close(this=$this)")
        mqtt.disconnect()
        mqtt.close()
    }

    /** Notifies all observers a new message for a specific topic has arrived */
    private fun notify(topic: String, message: String) {
        observers.forEach {
            it(topic, message)
        }
    }

    /** Used by observers so they can be notified when a message has arrived */
    fun subscribe(observer: (String, String) -> Unit) {
        observers.add(observer)
    }

    /** Publishes a message on a topic */
    fun publish(topic: String, value: String) {
        val message = MqttMessage()
        message.payload = value.toByteArray()
        message.qos = 0
        mqtt.publish(topic, message)
    }
}
