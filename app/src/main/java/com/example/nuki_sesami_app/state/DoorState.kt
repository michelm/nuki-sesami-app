package com.example.nuki_sesami_app.state

enum class DoorState(val value: Int) {
    Closed(0),  // Door is closed or about to close
    Opened(1),  // Door is open(ing)
    OpenHold(2), // Door is open(ing) and will be held open
    Unknown(255);

    companion object {
        fun from(value: Int): DoorState {
            return entries.firstOrNull { it.value == value } ?: Unknown
        }

        fun from(value: String): DoorState {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
