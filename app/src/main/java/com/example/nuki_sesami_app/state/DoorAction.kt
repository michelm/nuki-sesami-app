package com.example.nuki_sesami_app.state

enum class DoorAction(val value: Int) {
    None(0),
    Open(1),
    Close(2);

    companion object {
        fun from(value: Int): DoorAction {
            return entries.firstOrNull { it.value == value } ?: None
        }

        fun from(value: String): DoorAction {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
