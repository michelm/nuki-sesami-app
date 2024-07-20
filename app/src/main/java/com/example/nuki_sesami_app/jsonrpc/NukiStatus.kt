package com.example.nuki_sesami_app.jsonrpc

data class NukiStatus(
    var lock: Int,
    var doorsensor: Int,
)
