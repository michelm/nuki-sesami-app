package com.example.nuki_sesami_app.ui.misc

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.nuki_sesami_app.state.ConnectionType
import com.example.nuki_sesami_app.state.DoorAction
import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState
import com.example.nuki_sesami_app.R

@Composable
fun doorStateText(state: DoorState): String {
    return when (state) {
        DoorState.OpenHold -> stringResource(R.string.door_state_openhold)
        DoorState.Opened -> stringResource(R.string.door_state_opened)
        DoorState.Closed -> stringResource(R.string.door_state_closed)
        DoorState.Unknown -> stringResource(R.string.door_state_unknown)
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

@Composable
fun doorModeText(mode: DoorMode): String {
    return when(mode) {
        DoorMode.OpenHold -> stringResource(R.string.door_mode_openhold)
        DoorMode.OpenClose -> stringResource(R.string.door_mode_open_close)
        DoorMode.Unknown -> stringResource(R.string.door_mode_unknown)
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

@Composable
fun connectionTypeText(connection: ConnectionType): String {
    return when (connection) {
        ConnectionType.MQTT -> "mqtt"
        ConnectionType.Bluetooth -> "bluetooth"
        ConnectionType.Simulated -> "demo"
    }
}
