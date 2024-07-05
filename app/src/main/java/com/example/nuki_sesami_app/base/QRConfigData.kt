package com.example.nuki_sesami_app.base

data class QRConfigData(
    val nukiDeviceID: String,
    val mqttHostname: String,
    val mqttPort: Int,
    val mqttUsername: String,
    val mqttPassword: String,
    val bluetoothAddress: String,
    val bluetoothChannel: Int,
    val preferBluetooth: Boolean,
)
