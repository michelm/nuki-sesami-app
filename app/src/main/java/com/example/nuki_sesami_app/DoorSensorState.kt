package com.example.nuki_sesami_app

enum class DoorSensorState(val value: Int) {
    Deactivated(1),      // Door sensor not used
    DoorClosed(2),
    DoorOpened(3),
    DoorStateUnknown (4),
    Calibrating(5),
    Uncalibrated(16),
    Tampered(240),
    Unknown(255)
}
