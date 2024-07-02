package com.example.nuki_sesami_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun connectionStateText(connected: Boolean): String {
    return when (connected) {
        true -> stringResource(R.string.connection_state_connected)
        false -> stringResource(R.string.connection_state_not_connected)
    }
}

@Composable
fun DetailedStatusViewEntry(
    icon: ImageVector,
    caption: String,
    state: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* No action */ },
            enabled = false
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Localized Description"
            )
        }
        Text("$caption: ")
        Text(state, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DetailedStatusView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    var action by remember { mutableStateOf(sesami.doorAction.value) }
    var door by remember { mutableStateOf(sesami.doorState.value) }
    var mode by remember { mutableStateOf(sesami.doorMode.value) }
    var sensor by remember { mutableStateOf(sesami.doorSensor.value) }
    var lock by remember { mutableStateOf(sesami.lockState.value) }
    var serverVersion by remember { mutableStateOf(sesami.version.value) }
    var mqtt by remember { mutableStateOf(sesami.mqttConnected.value) }
    var mqttError by remember { mutableStateOf("") }
    var bluetooth by remember { mutableStateOf(false) }

    sesami.doorAction.subscribe { value: DoorAction -> action = value }
    sesami.doorState.subscribe { value: DoorState -> door = value }
    sesami.doorMode.subscribe { value: DoorMode -> mode = value }
    sesami.doorSensor.subscribe { value: DoorSensorState -> sensor = value }
    sesami.lockState.subscribe { value: LockState -> lock = value }
    sesami.version.subscribe { value: String -> serverVersion = value }
    sesami.mqttConnected.subscribe { value: Boolean -> mqtt = value }
    sesami.mqttError.subscribe { value: String -> mqttError = value }
    sesami.bluetoothConnected.subscribe { value: Boolean -> bluetooth = value }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            DetailedStatusViewEntry(
                Icons.Filled.CheckCircle,
                stringResource(R.string.detailed_status_view_entry_door_action), doorActionText(action))

            DetailedStatusViewEntry(Icons.Filled.Home,
                stringResource(R.string.detailed_status_view_entry_door_state), doorStateText(door))

            DetailedStatusViewEntry(Icons.Filled.Info,
                stringResource(R.string.detailed_status_view_entry_door_mode), doorModeText(mode))

            DetailedStatusViewEntry(Icons.Filled.Info,
                stringResource(R.string.detailed_status_view_entry_door_sensor), doorSensorText(sensor))

            DetailedStatusViewEntry(Icons.Filled.Info,
                stringResource(R.string.detailed_status_view_entry_lock_state), lockStateText(lock))

            DetailedStatusViewEntry(Icons.Filled.Info,
                stringResource(R.string.detailed_status_view_entry_server_version), serverVersion)

            DetailedStatusViewEntry(
                if (mqtt) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                "mqtt",
                connectionStateText(mqtt)
            )

            DetailedStatusViewEntry(
                if (bluetooth) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                "bluetooth",
                connectionStateText(bluetooth)
            )

            if (mqttError.isNotEmpty()) {
                TextField(
                    value = mqttError,
                    onValueChange = {},
                    label = { Text("MQTT error") },
                    singleLine = false
                )
            }
        }
    }
}
