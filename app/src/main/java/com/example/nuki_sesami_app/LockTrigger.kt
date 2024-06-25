package com.example.nuki_sesami_app

enum class LockTrigger(val value: Int) {
    SystemBluetooth(0),
    Reserved(1),
    Button(2),
    Automatic(3),       // e.g. time controlled
    AutoLock(4),
    HomeKit(171),
    MQTT(172)
}