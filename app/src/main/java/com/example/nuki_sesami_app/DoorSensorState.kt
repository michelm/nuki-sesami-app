package com.example.nuki_sesami_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

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
            return DoorSensorState.from(t ?: 0)
        }
    }
}

@Composable
fun doorSensorText(sensor: DoorSensorState): String {
    return when(sensor) {
        DoorSensorState.Deactivated -> stringResource(R.string.door_sensor_state_deactivated)
        DoorSensorState.DoorClosed -> stringResource(R.string.door_sensor_state_door_closed)
        DoorSensorState.DoorOpened -> stringResource(R.string.door_sensor_state_door_opened)
        DoorSensorState.DoorStateUnknown -> stringResource(R.string.door_sensor_state_door_state_unknown)
        DoorSensorState.Calibrating -> stringResource(R.string.door_sensor_state_calibrating)
        DoorSensorState.Uncalibrated -> stringResource(R.string.door_sensor_state_uncalibrated)
        DoorSensorState.Tampered -> stringResource(R.string.door_sensor_state_tampered)
        DoorSensorState.Unknown -> stringResource(R.string.door_sensor_state_unknown)
    }
}
