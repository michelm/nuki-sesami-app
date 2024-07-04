package com.example.nuki_sesami_app.state

enum class DoorRequestState(val value: Int) {
    None(0),    // No request
    Close(1),   // Close the door
    Open(2),    // Open the door briefly and then close it
    OpenHold(3); // Open the door and hold it open

    companion object {
        fun from(value: Int): DoorRequestState {
            return entries.firstOrNull { it.value == value } ?: None
        }

        fun from(value: String): DoorRequestState {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
