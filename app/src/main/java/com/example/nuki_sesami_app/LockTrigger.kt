package com.example.nuki_sesami_app

enum class LockTrigger(val value: Int) {
    SystemBluetooth(0),
    Reserved(1),
    Button(2),
    Automatic(3),       // e.g. time controlled
    AutoLock(4),
    HomeKit(171),
    MQTT(172),
    Undefined(255);

    companion object {
        fun from(value: Int): LockTrigger {
            return entries.firstOrNull { it.value == value } ?: Undefined
        }

        fun from(value: String): LockTrigger {
            val t = value.toIntOrNull()
            return LockTrigger.from(t ?: 0)
        }
    }
}