package com.example.nuki_sesami_app.connections

enum class ConnectionType(val value: Int) {
    MQTT(0),
    Bluetooth(1),
    Simulated(2); // dummy connection

    companion object {
        fun from(value: Int): ConnectionType {
            return entries.firstOrNull { it.value == value } ?: MQTT
        }

        fun from(value: String): ConnectionType {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
