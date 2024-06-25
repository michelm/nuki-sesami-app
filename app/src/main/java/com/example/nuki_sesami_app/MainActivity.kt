package com.example.nuki_sesami_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NukiSesamiAppTheme {
                MainScreen(
                    sesami = NukiSesamiClient(),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun doorActionText(action: DoorAction): String {
    return when (action) {
        DoorAction.Open -> stringResource(R.string.door_action_open)
        else -> stringResource(R.string.door_action_close)
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

@Composable
fun doorModeText(mode: DoorMode): String {
    return when(mode) {
        DoorMode.OpenHold -> stringResource(R.string.door_mode_openhold)
        DoorMode.OpenClose -> stringResource(R.string.door_mode_open_close)
        DoorMode.Unknown -> stringResource(R.string.door_mode_unknown)
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
        else -> stringResource(R.string.lock_state_undefined)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var viewSelected by remember { mutableStateOf(ViewSelected.LogicalView) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box {
                TopAppBar(
                    title = {
                        Text(
                            text = "Nuki Sesami",
                            maxLines = 1,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewSelected = ViewSelected.LogicalView }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = { viewSelected = ViewSelected.DetailedStatusView }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = { viewSelected = ViewSelected.SettingsView }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                )
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_logical_view)) },
                        onClick = { viewSelected = ViewSelected.LogicalView },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_detailed_status)) },
                        onClick = { viewSelected = ViewSelected.DetailedStatusView },
                        leadingIcon = { Icon(Icons.Outlined.DateRange, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_settings)) },
                        onClick = { viewSelected = ViewSelected.SettingsView },
                        leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_about)) },
                        onClick = { viewSelected = ViewSelected.AboutView },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                    )
                }
            }
        },
        content = { innerPadding ->
            MainContent(
                sesami = sesami,
                viewSelected = viewSelected,
                modifier = modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
fun MainContent(
    sesami: NukiSesamiClient,
    viewSelected: ViewSelected,
    modifier: Modifier = Modifier
) {
    when(viewSelected) {
        ViewSelected.LogicalView -> LogicalView(sesami, modifier)
        ViewSelected.DetailedStatusView -> DetailedStatusView(sesami, modifier)
        ViewSelected.SettingsView -> SettingsView(sesami, modifier)
        ViewSelected.AboutView -> AboutView(sesami, modifier)
    }
}

@Composable
fun LogicalView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    val action = doorActionText(sesami.doorAction)
    val lock = lockStateText(sesami.lockState)
    val door = doorStateText(sesami.doorState)
    var checked by remember { mutableStateOf(true) }

    Column (
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (verticalAlignment = Alignment.CenterVertically
        ){
            ElevatedButton(
                onClick = {
                    // TODO: Ask sesami to open or close the door
                }
            ) {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Localized Description")
                Spacer(modifier.padding(end = 5.dp))
                Text(action, fontSize = 36.sp)
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(checked = checked, onCheckedChange = {
                    checked = it
                    // TODO: Inform sesami to (de)activate the open hold mode
                }
            )
            Spacer(modifier.padding(end = 10.dp))
            Text(
                stringResource(R.string.door_mode_switch_text),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier.padding(10.dp))
        HorizontalDivider()

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* No action */ },
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Localized Description"
                    )
                }
                Text(door)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* No action */ },
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Localized Description"
                    )
                }
                Text(lock)
            }
        }
    }
}

@Composable
fun DetailedStatusView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Detailed status view")
        // TODO: add detailed status information
    }
}

@Composable
fun SettingsView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings view")
        // TODO: add application settings
    }
}

@Composable
fun AboutView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("About view")
        // TODO: add application and sesami service information
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NukiSesamiAppTheme {
        MainScreen(
            sesami = NukiSesamiClient(),
            modifier = Modifier
        )
    }
}
