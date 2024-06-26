package com.example.nuki_sesami_app

class NukiSesamiClient constructor (
    var nukiDeviceID: String,
    var mqttHostname: String,
    var mqttPort: String,
    var mqttUsername: String,
    var mqttPassword: String,
    var bluetoothAddress: String,
    var bluetoothChannel: String,
) {
    var doorMode: DoorMode = DoorMode.Unknown
    var doorState: DoorState = DoorState.Unknown
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.Open
    var lockState: LockState = LockState.Undefined

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

    fun closeDoor() {
        // TODO: implement me
    }

    fun openDoor(hold: Boolean) {
        // TODO: implement me
    }
}
