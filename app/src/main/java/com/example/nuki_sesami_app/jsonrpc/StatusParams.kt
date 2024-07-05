package com.example.nuki_sesami_app.jsonrpc

data class StatusParams(
    var nuki: NukiStatus,
    var door: DoorStatus,
    var relay: RelayStatus,
    var version: String,
)
