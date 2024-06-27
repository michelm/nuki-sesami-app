package com.example.nuki_sesami_app

import java.util.Timer
import java.util.TimerTask

open class NukiSesamiClient (
    var nukiDeviceID: String,
    var mqttHostname: String,
    var mqttPort: String,
    var mqttUsername: String,
    var mqttPassword: String,
    var bluetoothAddress: String,
    var bluetoothChannel: String
) {
    var doorMode: DoorMode = DoorMode.Unknown
    var doorState: DoorState = DoorState.Unknown
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.None
    var lockState: LockState = LockState.Undefined
    var mqttConnected = false
    var bluetoothConnected = false
    private val observers = ArrayList<() -> Unit>()

    fun observe(observer: () -> Unit) {
        observers.add(observer)
    }

    protected fun notifyObservers() {
        observers.forEach {
            it() // notifies the observer of the changes
        }
    }

    fun connect(
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

        // TODO: reconnect using mqtt or bluetooth
    }

    open fun closeDoor() {
        // TODO: implement me
    }

    open fun openDoor(hold: Boolean) {
        // TODO: implement me
    }
}

class DummyNukiSesamiClient(
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
    init {
        doorMode = DoorMode.OpenClose
        doorState = DoorState.Closed
        doorSensor = DoorSensorState.DoorClosed
        doorAction = DoorAction.Open
        lockState = LockState.Unlocked
        mqttConnected = true
        bluetoothConnected = false
    }

    override fun closeDoor() {
        doorMode = DoorMode.OpenClose
        doorState = DoorState.Closed
        doorAction = DoorAction.Open
        lockState = LockState.Unlocked
        doorSensor = DoorSensorState.DoorClosed
        notifyObservers()
    }

    override fun openDoor(hold: Boolean) {
        doorMode = if (hold) DoorMode.OpenHold else DoorMode.OpenClose
        doorState = DoorState.Opened
        doorAction = DoorAction.Close
        lockState = LockState.Unlatched
        doorSensor = DoorSensorState.DoorOpened
        notifyObservers()
    }
}
