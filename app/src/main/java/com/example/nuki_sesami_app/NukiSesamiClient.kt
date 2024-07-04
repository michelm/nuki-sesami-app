package com.example.nuki_sesami_app

import android.app.Activity
import android.content.Context
import com.example.nuki_sesami_app.base.ObservableState
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_DEVICE_ID
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_PORT
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_USERNAME
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.state.ConnectionType
import com.example.nuki_sesami_app.state.DoorAction
import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorRequestState
import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState

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

    private var mqtt: MqttClient? = null

    private fun getNukiSesamiMqtt(context: Context): MqttClient {
        val mqtt = MqttClient(
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
