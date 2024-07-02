package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

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
            return DoorState.from(t ?: 0)
        }
    }
}

@Composable
fun doorStateText(state: DoorState): String {
    return when (state) {
        DoorState.OpenHold -> stringResource(R.string.door_state_openhold)
        DoorState.Opened -> stringResource(R.string.door_state_opened)
        DoorState.Closed -> stringResource(R.string.door_state_closed)
        DoorState.Unknown -> stringResource(R.string.door_state_unknown)
    }
}
