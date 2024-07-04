package com.example.nuki_sesami_app.state

enum class LockAction(val value: Int) {
    Unlock(1),      // Activate RTO
    Lock(2),        // Deactivate RTO
    Unlatch(3),     // Electric strike actuation
    LockAndGo1(4),  // Lock&Go; activate continuous mode
    LockAndGo2(5),  // Lock&Go with unlatch deactivate continuous mode
    FullLock(6),
    Fob(80),        // Fob (without action)
    Button(90),     // Button (without action)
    None(255);

    companion object {
        fun from(value: Int): LockAction {
            return entries.firstOrNull { it.value == value } ?: None
        }

        fun from(value: String): LockAction {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
