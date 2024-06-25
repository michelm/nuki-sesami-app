package com.example.nuki_sesami_app

class NukiSesamiClient {
    var doorMode: DoorMode = DoorMode.Unknown
    var doorState: DoorState = DoorState.Unknown
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.Open
    var lockState: LockState = LockState.Undefined
}
