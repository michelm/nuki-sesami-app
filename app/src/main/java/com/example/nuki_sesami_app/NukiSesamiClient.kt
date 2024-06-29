package com.example.nuki_sesami_app

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.random.Random

class NukiSesamiMqtt(
    hostname: String,
    port: String,
    private var username: String,
    private var password: String,
    clientId: String,
    private val nukiDeviceID: String,
): MqttCallback, IMqttActionListener {
    /** Observable state, will be set to true when connected */
    var connected = ObservableState(false)

    /** Observable state, will be set in case of (connection) errors */
    var error = ObservableState("")

    /** Contains last received message of topics */
    val messages = mutableMapOf<String, String>()

    /** List of subscribers to message events */
    private val observers = ArrayList<(String, String) -> Unit>()

    /** Contains the actual PAHO client handle */
    private var mqtt: MqttAsyncClient = MqttAsyncClient(
        "tcp://$hostname:$port", clientId, MemoryPersistence())

    /** Used for retry connect logic */
    private var reconnectTimer: Timer? = null

    /** Reconnect interval in seconds */
    private var reconnectInterval: Long = 0L

    /** Disconnect timeout in milliseconds */
    private val disconnectTimeout: Long = 2000L

    init {
        mqtt.setCallback(this)
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

    private fun connect() {
        if (connected.value) {
            return
        }

        val options = MqttConnectOptions()
        options.isCleanSession = false
        if (username.isNotEmpty() && password.isNotEmpty()) {
            options.password = password.toCharArray()
            options.userName = username
        }

        try {
            error.value = ""
            mqtt.connect(options, this)
        } catch (ex: MqttException) {
            error.value = ex.toString()
        } catch (ex: Exception) {
            error.value = ex.toString()
        }
    }

    private fun disconnect() {
        mqtt.disconnectForcibly(disconnectTimeout)
        connected.value = false
        error.value = ""
    }

    private fun scheduleReconnect() {
        reconnectTimer?.cancel()
        reconnectTimer?.purge()
        reconnectTimer = null

        if (reconnectInterval == 0L) {
            return
        }

        reconnectTimer = timer(
            name = "NukiSesamiMqttRetryTimer",
            daemon = false,
            initialDelay = 1000, // 1[s]
            period = reconnectInterval * 1000,
            action = {
                if (!connected.value) {
                    connect()
                }
            }
        )
    }

    /** Activates the MQTT connection
     *  - Retry interval is in seconds
     *  - If the retry interval is not specified the client will not reconnect
     */
    fun activate(retryInterval: Long = 0) {
        if (connected.value) {
            return
        }

        connect()
        reconnectInterval = retryInterval
        scheduleReconnect()
    }

    /** Deactivates the MQTT connection */
    fun deactivate() {
        disconnect()
        reconnectInterval = 0
        reconnectTimer?.cancel()
        reconnectTimer?.purge()
        reconnectTimer = null
    }

    // MqttCallback
    override fun connectionLost(cause: Throwable?) {
        connected.value = false
        error.value = cause.toString()
        scheduleReconnect()
    }

    // MqttCallback
    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic == null || message == null) {
            return
        }

        val msg = message.payload.decodeToString()
        messages[topic] = msg
        notify(topic, msg)
    }

    // MqttCallback
    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // no action
    }

    // IMqttActionListener: connect callback
    override fun onSuccess(asyncActionToken: IMqttToken?) {
        this.reconnectTimer?.cancel()
        this.reconnectTimer?.purge()
        this.reconnectTimer = null

        connected.value = true
        error.value = ""
        mqtt.subscribe("nuki/${nukiDeviceID}/state", 0)
        mqtt.subscribe("nuki/${nukiDeviceID}/doorsensorState", 0)
        mqtt.subscribe("sesami/${nukiDeviceID}/state", 0)
        mqtt.subscribe("sesami/${nukiDeviceID}/mode", 0)
        mqtt.subscribe("sesami/${nukiDeviceID}/version", 0)
    }

    // IMqttActionListener: connect callback
    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        connected.value = false
        error.value = exception.toString()
    }
}

open class NukiSesamiClient (
    var nukiDeviceID: String,
    var mqttHostname: String,
    var mqttPort: String,
    var mqttUsername: String,
    var mqttPassword: String,
    var bluetoothAddress: String,
    var bluetoothChannel: String
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

    var version = ObservableState("?")
        protected set

    var bluetoothConnected = ObservableState(false)
        protected set

    var mqttConnected = ObservableState(false)
        protected set

    var mqttError = ObservableState("")
        protected set

    private val mqttReconnectInterval = 5L
    private val mqttClientId = "NukiSesamiApp" // UUID.randomUUID().toString()
    private var mqtt: NukiSesamiMqtt? = null

    private fun getNukiSesamiMqtt(): NukiSesamiMqtt {
        val mqtt = NukiSesamiMqtt(
            mqttHostname,
            mqttPort,
            mqttUsername,
            mqttPassword,
            mqttClientId,
            nukiDeviceID
        )

        mqtt.connected.subscribe { value -> mqttConnected.value = value }
        mqtt.error.subscribe { value -> mqttError.value = value }

        mqtt.subscribe { topic, message ->
            when(topic) {
                "sesami/${nukiDeviceID}/version" -> version.value = message
                "sesami/${nukiDeviceID}/state" -> {
                    doorState.value = DoorState.from(message)
                    doorAction.value = when(doorState.value) {
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

        return mqtt
    }

    open fun activate() {
        if (mqtt == null) {
            mqtt = getNukiSesamiMqtt()
            mqtt!!.activate(mqttReconnectInterval)
        }
    }

    open fun deactivate() {
        if (mqtt != null) {
            mqtt!!.deactivate()
            mqtt = null
        }
    }

    open fun configure(
        nukiDeviceID: String,
        mqttHostname: String,
        mqttPort: String,
        mqttUsername: String,
        mqttPassword: String,
        bluetoothAddress: String,
        bluetoothChannel: String,
    ) {
        this.nukiDeviceID = nukiDeviceID
        this.mqttHostname = mqttHostname
        this.mqttPort = mqttPort
        this.mqttUsername = mqttUsername
        this.mqttPassword = mqttPassword
        this.bluetoothAddress = bluetoothAddress
        this.bluetoothChannel = bluetoothChannel
    }

    open fun openDoor(hold: Boolean) {
        val request = if (hold) DoorRequestState.OpenHold else DoorRequestState.Open

        if (mqtt!!.connected.value) {
            mqtt!!.publish("sesami/${nukiDeviceID}/request/state", request.value.toString())
        }
    }

    open fun closeDoor() {
        val request = DoorRequestState.Close

        if (mqtt!!.connected.value) {
            mqtt!!.publish("sesami/${nukiDeviceID}/request/state", request.value.toString())
        }
    }
}

class NukiSesamiClientSimulation(
    nukiDeviceID: String,
    mqttHostname: String,
    mqttPort: String,
    mqttUsername: String,
    mqttPassword: String,
    bluetoothAddress: String,
    bluetoothChannel: String,
) : NukiSesamiClient(
    nukiDeviceID,
    mqttHostname,
    mqttPort,
    mqttUsername,
    mqttPassword,
    bluetoothAddress,
    bluetoothChannel,
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

    override fun openDoor(hold: Boolean) {
        doorMode.value = if (hold) DoorMode.OpenHold else DoorMode.OpenClose
        doorState.value = DoorState.Opened
        doorAction.value = DoorAction.Close
        lockState.value = LockState.Unlatched
        doorSensor.value = DoorSensorState.DoorOpened
        this.activate()
    }

    override fun closeDoor() {
        doorMode.value = DoorMode.OpenClose
        doorState.value = DoorState.Closed
        doorAction.value = DoorAction.Open
        lockState.value = LockState.Unlocked
        doorSensor.value = DoorSensorState.DoorClosed
        this.deactivate()
    }

    // Simulate start connection to MQTT / Bluetooth
    override fun activate() {
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

fun getSesamiClient(preferences: UserPreferences, activate: Boolean,
                    simulation: Boolean = false): NukiSesamiClient {
    val nukiDeviceID = preferences.load(
        R.string.preferences_key_nuki_device_id, "3807B7EC")
    val mqttHostname = preferences.load(
        R.string.preferences_key_mqtt_hostname, "raspi-door")
    val mqttPort = preferences.load(
        R.string.preferences_key_mqtt_port, "1883")
    val mqttUsername = preferences.load(
        R.string.preferences_key_mqtt_username, "sesami")
    val mqttPassword = preferences.load(
        R.string.preferences_key_mqtt_password, "")
    val bluetoothAddress = preferences.load(
        R.string.preferences_key_bluetooth_address, "B8:27:EB:B9:2A:F0")
    val bluetoothChannel = preferences.load(
        R.string.preferences_key_bluetooth_channel, "4")

    if (simulation) {
        val sesami =  NukiSesamiClientSimulation(
            nukiDeviceID,
            mqttHostname,
            mqttPort,
            mqttUsername,
            mqttPassword,
            bluetoothAddress,
            bluetoothChannel,
        )

        if (activate) {
            sesami.activate()
        }

        return sesami
    }

    val sesami = NukiSesamiClient(
        nukiDeviceID,
        mqttHostname,
        mqttPort,
        mqttUsername,
        mqttPassword,
        bluetoothAddress,
        bluetoothChannel,
    )

    if (activate) {
        sesami.activate()
    }

    return sesami
}
