package com.example.nuki_sesami_app

import android.app.Activity
import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.random.Random

const val NUKI_SESAMI_DEFAULT_DEVICE_ID = "3807B7EC"
const val NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME = "192.168.178.56"
const val NUKI_SESAMI_DEFAULT_MQTT_PORT = 1883
const val NUKI_SESAMI_DEFAULT_MQTT_USERNAME = "sesami"
const val NUKI_SESAMI_DEFAULT_MQTT_PASSWORD = ""
const val NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS = "B8:27:EB:B9:2A:F0"
const val NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL = 4

const val NUKI_SESAMI_MIN_MQTT_PORT = 1
const val NUKI_SESAMI_MAX_MQTT_PORT = 65535

const val NUKI_SESAMI_MIN_BLUETOOTH_CHANNEL = 1
const val NUKI_SESAMI_MAX_BLUETOOTH_CHANNEL = 9

class NukiSesamiMqtt(
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

open class NukiSesamiClient (
    private var nukiDeviceID: String = NUKI_SESAMI_DEFAULT_DEVICE_ID,
    private var mqttHostname: String = NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME,
    private var mqttPort: Int = NUKI_SESAMI_DEFAULT_MQTT_PORT,
    private var mqttUsername: String = NUKI_SESAMI_DEFAULT_MQTT_USERNAME,
    private var mqttPassword: String = NUKI_SESAMI_DEFAULT_MQTT_PASSWORD,
    private var bluetoothAddress: String = NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS,
    private var bluetoothChannel: Int = NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
) {
    var doorState = ObservableState(DoorState.Unknown)
        protected set

    var doorMode = ObservableState(DoorMode.Unknown)
        protected set

    var doorAction = ObservableState(DoorAction.None)
        protected set

    var doorSensor = ObservableState(DoorSensorState.Unknown)
        protected set

    var lockState = ObservableState(LockState.Undefined)
        protected set

    var version = ObservableState("0.0.0")
        protected set

    var connectionType = ObservableState(ConnectionType.MQTT)
        protected set

    var connectionError = ObservableState("")
        protected set

    var connected = ObservableState(false)
        protected set

    private var mqtt: NukiSesamiMqtt? = null

    private fun getNukiSesamiMqtt(context: Context): NukiSesamiMqtt {
        val mqtt = NukiSesamiMqtt(
            mqttHostname,
            mqttPort,
            mqttUsername,
            mqttPassword,
            nukiDeviceID
        )

        mqtt.connected.subscribe { value ->
            (context as Activity).runOnUiThread {
                connected.value = value
            }
        }

        mqtt.error.subscribe { value ->
            (context as Activity).runOnUiThread {
                connectionError.value = value
            }
        }

        mqtt.subscribe { topic, message ->
            (context as Activity).runOnUiThread {
                when (topic) {
                    "sesami/${nukiDeviceID}/version" -> version.value = message
                    "sesami/${nukiDeviceID}/state" -> {
                        doorState.value = DoorState.from(message)
                        doorAction.value = when (doorState.value) {
                            DoorState.Closed -> DoorAction.Open
                            DoorState.OpenHold -> DoorAction.Close
                            DoorState.Opened -> DoorAction.None
                            DoorState.Unknown -> DoorAction.None
                        }
                    }

                    "sesami/${nukiDeviceID}/mode" -> doorMode.value = DoorMode.from(message)
                    "nuki/${nukiDeviceID}/doorsensorState" -> {
                        doorSensor.value = DoorSensorState.from(message)
                    }

                    "nuki/${nukiDeviceID}/state" -> lockState.value = LockState.from(message)
                }
            }
        }

        return mqtt
    }

    open fun simulated(): Boolean { return false }

    open fun activate(context: Context) {
        if (connectionType.value == ConnectionType.MQTT) {
            if (mqtt == null) {
                mqtt = getNukiSesamiMqtt(context)
            }
        } else { // Bluetooth
            // TODO: implement me
        }
    }

    open fun deactivate() {
        if (connectionType.value == ConnectionType.MQTT) {
            mqtt!!.close()
            mqtt = null
        } else { // Bluetooth
            // TODO: implement me
        }
    }

    fun configure(preferences: UserPreferences) {
        nukiDeviceID = preferences.load(
            R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID)
        mqttHostname = preferences.load(
            R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME)
        mqttPort = preferences.load(
            R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT)
        mqttUsername = preferences.load(
            R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME)
        mqttPassword = preferences.load(
            R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD)
        bluetoothAddress = preferences.load(
            R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS)
        bluetoothChannel = preferences.load(
            R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL)

        val preferBluetooth = preferences.load(
            R.string.preferences_key_prefer_bluetooth, false)
        connectionType.value = if (preferBluetooth) ConnectionType.Bluetooth else ConnectionType.MQTT
    }

    open fun openDoor(hold: Boolean) {
        val request = if (hold) DoorRequestState.OpenHold else DoorRequestState.Open

        if (connectionType.value == ConnectionType.MQTT) {
            if (mqtt!!.connected.value) {
                mqtt!!.publish("sesami/${nukiDeviceID}/request/state", request.value.toString())
            }
        } else { // Bluetooth
            // TODO: implement me
        }
    }

    open fun closeDoor() {
        val request = DoorRequestState.Close

        if (connectionType.value == ConnectionType.MQTT) {
            if (mqtt!!.connected.value) {
                mqtt!!.publish("sesami/${nukiDeviceID}/request/state", request.value.toString())
            }
        } else { // Bluetooth
            // TODO: implement me
        }
    }
}

class NukiSesamiClientSimulation: NukiSesamiClient(
) {
    /** Simulation timer used to mimic to some dummy behavior */
    private var simulationTimer: Timer? = null

    init {
        doorMode.value = DoorMode.OpenClose
        doorState.value = DoorState.Closed
        doorSensor.value = DoorSensorState.DoorClosed
        doorAction.value = DoorAction.Open
        lockState.value = LockState.Unlocked
        version.value = "1.2.3"
    }

    override fun simulated(): Boolean { return true }

    override fun openDoor(hold: Boolean) {
        doorMode.value = if (hold) DoorMode.OpenHold else DoorMode.OpenClose
        doorState.value = DoorState.Opened
        doorAction.value = DoorAction.Close
        lockState.value = LockState.Unlatched
        doorSensor.value = DoorSensorState.DoorOpened
    }

    override fun closeDoor() {
        doorMode.value = DoorMode.OpenClose
        doorState.value = DoorState.Closed
        doorAction.value = DoorAction.Open
        lockState.value = LockState.Unlocked
        doorSensor.value = DoorSensorState.DoorClosed
    }

    // Simulate start connection to MQTT / Bluetooth
    override fun activate(context: Context) {
        if (this.simulationTimer != null) {
            return
        }

        this.simulationTimer = timer(
            name = "NukiSesamiSimulationTimer",
            daemon = false,
            initialDelay = 0,
            period = 1000,
            action = {
                doorState.value = DoorState.from(Random.nextInt(1,3))
                doorMode.value = DoorMode.from(Random.nextInt(0,2))
                lockState.value = LockState.from(Random.nextInt(1, 8))
                doorSensor.value = DoorSensorState.from(Random.nextInt(1, 6))
                val n = Random.nextInt(0, 10)
                version.value = "$n.$n.$n"
                connected.value = Random.nextBoolean()
                connectionType.value = ConnectionType.from(Random.nextInt(0,2))
            }
        )
    }

    // Simulate terminate connection with MQTT / Bluetooth
    override fun deactivate() {
        this.simulationTimer?.cancel()
        this.simulationTimer?.purge()
        this.simulationTimer = null
        this.lockState.value = LockState.Undefined
    }
}
