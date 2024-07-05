package com.example.nuki_sesami_app.jsonrpc

data class RelayStatus(
    var openclose: Boolean = false,
    var openhold: Boolean = false,
    var opendoor: Boolean = false,
)
