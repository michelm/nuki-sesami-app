package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

enum class DoorMode(val value: Int) {
    OpenClose(0),  // Door is open for a brief moment, the actual time is defined by the
                        // ERREKA 'Smart Evolution' electric door controller
    OpenHold(1),    // Door will be held open until the push button is pressed again
    Unknown(255);

    companion object {
        fun from(value: Int): DoorMode {
            return entries.firstOrNull { it.value == value } ?: Unknown
        }

        fun from(value: String): DoorMode {
            val t = value.toIntOrNull()
            return DoorMode.from(t ?: 0)
        }
    }
}

@Composable
fun doorModeText(mode: DoorMode): String {
    return when(mode) {
        DoorMode.OpenHold -> stringResource(R.string.door_mode_openhold)
        DoorMode.OpenClose -> stringResource(R.string.door_mode_open_close)
        DoorMode.Unknown -> stringResource(R.string.door_mode_unknown)
    }
}
