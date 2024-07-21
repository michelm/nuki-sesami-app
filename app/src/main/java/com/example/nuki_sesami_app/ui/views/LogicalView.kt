package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuki_sesami_app.connections.ConnectionType
import com.example.nuki_sesami_app.state.DoorAction
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState
import com.example.nuki_sesami_app.NukiSesamiClient
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.ui.misc.doorActionText
import com.example.nuki_sesami_app.ui.misc.doorStateText
import com.example.nuki_sesami_app.ui.misc.lockStateText
import com.example.nuki_sesami_app.ui.misc.connectionTypeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogicalViewDetailsEntry(
    icon: ImageVector,
    text: String,
    tooltip: Int,
    tint: Color? = null
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(stringResource(tooltip)) } },
        state = rememberTooltipState(),
        focusable = true
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
            Text(text)
        }
    }
}

@Composable
fun LogicalView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier,
    preferences: UserPreferences,
) {
    var action by remember { mutableStateOf(sesami.doorAction.value) }
    var door by remember { mutableStateOf(sesami.doorState.value) }
    var lock by remember { mutableStateOf(sesami.lockState.value) }
    var connected by remember { mutableStateOf(sesami.connected.value) }
    var connectionType by remember { mutableStateOf(sesami.connectionType.value) }
    var hold by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_switch_openhold, true)) }

    sesami.doorAction.subscribe { value: DoorAction -> action = value }
    sesami.doorState.subscribe { value: DoorState -> door = value }
    sesami.lockState.subscribe { value: LockState -> lock = value }
    sesami.connected.subscribe { value: Boolean ->
        connected = value
    }
    sesami.connectionType.subscribe { value: ConnectionType ->
        connectionType = value
    }

    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (verticalAlignment = Alignment.CenterVertically
        ){
            ElevatedButton(
                modifier = Modifier
                    .padding(20.dp)
                    .size(210.dp),
                enabled = action != DoorAction.None,
                onClick = {
                    when(action) {
                        DoorAction.None -> { /* no action */  }
                        DoorAction.Close -> sesami.closeDoor()
                        DoorAction.Open -> sesami.openDoor(hold = hold)
                    }
                },
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .padding(end = 1.dp)
                        .size(42.dp),
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Localized Description")
                Text(doorActionText(action), fontSize = 44.sp)
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier.padding(end = 5.dp),
                enabled = action == DoorAction.Open,
                checked = hold,
                onCheckedChange = {
                    hold = it
                    preferences.save(R.string.preferences_key_switch_openhold, it)
                }
            )
            Text(
                stringResource(R.string.door_mode_switch_text),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(20.dp))

        Row (verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                LogicalViewDetailsEntry(
                    Icons.Filled.Home,
                    doorStateText(door),
                    R.string.tooltip_electric_door_state
                )

                LogicalViewDetailsEntry(
                    Icons.Filled.Lock,
                    lockStateText(lock),
                    R.string.tooltip_smart_lock_state
                )

                LogicalViewDetailsEntry(
                    if (connected) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    connectionTypeText(connectionType),
                    R.string.tooltip_sesami_connection_state,
                    tint = if (connected) null else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogicalViewPreview() {
    val context = LocalContext.current
    LogicalView(
        sesami = NukiSesamiClient(context, null, null),
        modifier = Modifier,
        preferences = UserPreferences(),
    )
}