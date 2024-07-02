package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

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
            return LockState.from(t ?: 0)
        }
    }
}

@Composable
fun lockStateText(lockState: LockState): String {
    return when(lockState) {
        LockState.Uncalibrated -> stringResource(R.string.lock_state_uncalibrated)
        LockState.Locked  -> stringResource(R.string.lock_state_locked)
        LockState.Unlocking  -> stringResource(R.string.lock_state_unlocking)
        LockState.Unlocked -> stringResource(R.string.lock_state_unlocked)
        LockState.Locking -> stringResource(R.string.lock_state_locking)
        LockState.Unlatched -> stringResource(R.string.lock_state_unlatched)
        LockState.Unlocked2 -> stringResource(R.string.lock_state_unlocked2)
        LockState.Unlatching -> stringResource(R.string.lock_state_unlatching)
        LockState.BootRun -> stringResource(R.string.lock_state_boot_run)
        LockState.MotorBlocked -> stringResource(R.string.lock_state_motor_blocked)
        LockState.Undefined -> stringResource(R.string.lock_state_undefined)
    }
}
