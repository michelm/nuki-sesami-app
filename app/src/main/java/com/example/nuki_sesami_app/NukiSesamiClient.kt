package com.example.nuki_sesami_app

class NukiSesamiClient {
    var doorMode: DoorMode = DoorMode.OpenClose
    var doorState: DoorState = DoorState.Closed
    var doorSensor: DoorSensorState = DoorSensorState.DoorStateUnknown
    var doorAction: DoorAction = DoorAction.Open
    var lockState: LockState = LockState.Undefined
}
