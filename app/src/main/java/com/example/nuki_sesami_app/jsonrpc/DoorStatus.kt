package com.example.nuki_sesami_app.jsonrpc

import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorState

data class DoorStatus(
    var state: Int,
    var mode: Int,
)
