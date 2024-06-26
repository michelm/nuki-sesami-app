package com.example.nuki_sesami_app

class NukiSesamiClient {
    var doorMode: DoorMode = DoorMode.Unknown
    var doorState: DoorState = DoorState.Unknown
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.Open
    var lockState: LockState = LockState.Undefined
    var mqttHostname = "localhost"
    var mqttPort = "1883"
    var mqttUsername = "john.doe"
    var mqttPassword = "secret"
    var bluetoothAddress = "B8:27:EB:B9:2A:F0"
    var bluetoothChannel = "4"

    fun useSettings(
        mqttHostname: String,
        mqttPort: String,
        mqttUsername: String,
        mqttPassword: String,
        bluetoothAddress: String,
        bluetoothChannel: String,
    ) {
        this.mqttHostname = mqttHostname
        this.mqttPort = mqttPort
        this.mqttUsername = mqttUsername
        this.mqttPassword = mqttPassword
        this.bluetoothAddress = bluetoothAddress
        this.bluetoothChannel = bluetoothChannel
    }

    fun closeDoor() {
        // TODO: implement me
    }

    fun openDoor(hold: Boolean) {
        // TODO: implement me
    }
}
