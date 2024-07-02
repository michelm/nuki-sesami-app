package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

enum class DoorAction(val value: Int) {
    None(0),
    Open(1),
    Close(2);

    companion object {
        fun from(value: Int): DoorAction {
            return entries.firstOrNull { it.value == value } ?: None
        }

        fun from(value: String): DoorAction {
            val t = value.toIntOrNull()
            return DoorAction.from(t ?: 0)
        }
    }
}

@Composable
fun doorActionText(action: DoorAction): String {
    return when (action) {
        DoorAction.None -> stringResource(R.string.door_action_none)
        DoorAction.Open -> stringResource(R.string.door_action_open)
        DoorAction.Close -> stringResource(R.string.door_action_close)
    }
}
