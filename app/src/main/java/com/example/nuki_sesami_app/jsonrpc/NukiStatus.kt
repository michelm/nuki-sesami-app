package com.example.nuki_sesami_app.jsonrpc

import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.LockState

data class NukiStatus(
    var lock: Int,
    var doorsensor: Int,
)
