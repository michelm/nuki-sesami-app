package com.example.nuki_sesami_app

enum class LockAction(val value: Int) {
    Unlock(1),      // Activate RTO
    Lock(2),        // Deactivate RTO
    Unlatch(3),     // Electric strike actuation
    LockAndGo1(4),  // Lock&Go; activate continuous mode
    LockAndGo2(5),  // Lock&Go with unlatch deactivate continuous mode
    FullLock(6),
    Fob(80),        // Fob (without action)
    Button(90)      // Button (without action)
}
