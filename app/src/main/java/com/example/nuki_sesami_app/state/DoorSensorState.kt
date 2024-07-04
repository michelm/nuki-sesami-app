package com.example.nuki_sesami_app.state

enum class DoorSensorState(val value: Int) {
    Deactivated(1),      // Door sensor not used
    DoorClosed(2),
    DoorOpened(3),
    DoorStateUnknown (4),
    Calibrating(5),
    Uncalibrated(16),
    Tampered(240),
    Unknown(255);

    companion object {
        fun from(value: Int): DoorSensorState {
            return entries.firstOrNull { it.value == value } ?: Unknown
        }

        fun from(value: String): DoorSensorState {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
