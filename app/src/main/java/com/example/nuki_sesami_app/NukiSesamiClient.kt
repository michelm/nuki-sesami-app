package com.example.nuki_sesami_app

class NukiSesamiClient {
    var doorMode: DoorMode = DoorMode.Undefined
    var doorState: DoorState = DoorState.Undefined
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.Open
    var lockState: LockState = LockState.Undefined
}
