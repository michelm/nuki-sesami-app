package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nuki_sesami_app.state.ConnectionType
import com.example.nuki_sesami_app.state.DoorAction
import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState
import com.example.nuki_sesami_app.NukiSesamiClient
import com.example.nuki_sesami_app.NukiSesamiSimulation
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.ui.misc.doorActionText
import com.example.nuki_sesami_app.ui.misc.doorModeText
import com.example.nuki_sesami_app.ui.misc.doorSensorText
import com.example.nuki_sesami_app.ui.misc.doorStateText
import com.example.nuki_sesami_app.ui.misc.lockStateText
import com.example.nuki_sesami_app.ui.misc.connectionTypeText

@Composable
fun DetailedStatusViewEntry(
    icon: ImageVector,
    caption: String,
    state: String,
    tint: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* No action */ },
            enabled = false
        ) {
            if (tint != null) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
            } else {
                Icon(imageVector = icon, contentDescription = null)
            }
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
    var connected by remember { mutableStateOf(sesami.connected.value) }
    var connectionType by remember { mutableStateOf(sesami.connectionType.value) }
    var connectionError by remember { mutableStateOf(sesami.connectionError.value) }

    sesami.doorAction.subscribe { value: DoorAction -> action = value }
    sesami.doorState.subscribe { value: DoorState -> door = value }
    sesami.doorMode.subscribe { value: DoorMode -> mode = value }
    sesami.doorSensor.subscribe { value: DoorSensorState -> sensor = value }
    sesami.lockState.subscribe { value: LockState -> lock = value }
    sesami.version.subscribe { value: String -> serverVersion = value }
    sesami.connectionType.subscribe { value: ConnectionType -> connectionType = value }
    sesami.connectionError.subscribe { value: String -> connectionError = value }
    sesami.connected.subscribe { value: Boolean -> connected = value }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        DetailedStatusViewEntry(
            Icons.Filled.CheckCircle,
            stringResource(R.string.detailed_status_view_door_action), doorActionText(action)
        )

        DetailedStatusViewEntry(Icons.Filled.Home,
            stringResource(R.string.detailed_status_view_door_state), doorStateText(door)
        )

        DetailedStatusViewEntry(Icons.Filled.Info,
            stringResource(R.string.detailed_status_view_door_mode), doorModeText(mode)
        )

        DetailedStatusViewEntry(Icons.Filled.Info,
            stringResource(R.string.detailed_status_view_door_sensor), doorSensorText(sensor)
        )

        DetailedStatusViewEntry(Icons.Filled.Info,
            stringResource(R.string.detailed_status_view_lock_state), lockStateText(lock)
        )

        DetailedStatusViewEntry(Icons.Filled.Info,
            stringResource(R.string.detailed_status_view_server_version), serverVersion)

        DetailedStatusViewEntry(
            icon = if (connected) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            caption = stringResource(R.string.detailed_status_view_connected),
            state = connected.toString(),
            tint = if (connected) null else MaterialTheme.colorScheme.error
        )

        DetailedStatusViewEntry(Icons.Filled.Info,
            stringResource(R.string.detailed_status_view_connection_type), connectionTypeText(connectionType)
        )

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.padding(5.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.padding(start = 5.dp),
                value = if (connectionError.isNotEmpty()) connectionError else "<none>",
                onValueChange = {},
                label = { Text(stringResource(R.string.detailed_status_view_connection_error)) },
                singleLine = false,
                maxLines = 10
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedStatusViewPreview() {
    DetailedStatusView(
        modifier = Modifier,
        sesami = NukiSesamiSimulation()
    )
}
