package com.example.nuki_sesami_app.state

enum class LockState(val value: Int) {
    Uncalibrated(0),    // Untrained
    Locked(1),          // Online
    Unlocking(2),
    Unlocked(3),        // RTO Active
    Locking(4),
    Unlatched(5),       // Open
    Unlocked2(6),       // Lock-N-Go
    Unlatching(7),      // Opening
    BootRun(253),
    MotorBlocked(254),
    Undefined(255);

    companion object {
        fun from(value: Int): LockState {
            return entries.firstOrNull { it.value == value } ?: Undefined
        }

        fun from(value: String): LockState {
            val t = value.toIntOrNull()
            return from(t ?: 0)
        }
    }
}
