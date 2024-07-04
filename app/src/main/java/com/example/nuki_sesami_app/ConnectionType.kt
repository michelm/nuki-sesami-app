package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable

enum class ConnectionType(val value: Int) {
    MQTT(0),
    Bluetooth(1);

    companion object {
        fun from(value: Int): ConnectionType {
            return entries.firstOrNull { it.value == value } ?: MQTT
        }

        fun from(value: String): ConnectionType {
            val t = value.toIntOrNull()
            return ConnectionType.from(t ?: 0)
        }
    }
}

@Composable
fun connectionTypeText(connection: ConnectionType): String {
    return when (connection) {
        ConnectionType.MQTT -> "mqtt"
        ConnectionType.Bluetooth -> "bluetooth"
    }
}
