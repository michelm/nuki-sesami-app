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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    var appBarTitleRID by remember { mutableStateOf(R.string.app_bar_title_home) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(appBarTitleRID),
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
                        IconButton(onClick = {
                            viewSelected = ViewSelected.LogicalView
                            appBarTitleRID = R.string.app_bar_title_home
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = {
                            viewSelected = ViewSelected.DetailedStatusView
                            appBarTitleRID = R.string.app_bar_title_detailed_status
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = {
                            viewSelected = ViewSelected.SettingsView
                            appBarTitleRID = R.string.app_bar_title_settings
                        }) {
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
                        onClick = { viewSelected = ViewSelected.LogicalView
                                    appBarTitleRID = R.string.app_bar_title_home
                                    menuExpanded = false
                                  },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_detailed_status)) },
                        onClick = { viewSelected = ViewSelected.DetailedStatusView
                                    appBarTitleRID = R.string.app_bar_title_detailed_status
                                    menuExpanded = false
                                  },
                        leadingIcon = { Icon(Icons.Outlined.DateRange, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_settings)) },
                        onClick = { viewSelected = ViewSelected.SettingsView
                                    appBarTitleRID = R.string.app_bar_title_settings
                                    menuExpanded = false
                                  },
                        leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_about)) },
                        onClick = { viewSelected = ViewSelected.AboutView
                                    appBarTitleRID = R.string.app_bar_title_about
                                    menuExpanded = false
                                  },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                    )
                }
            }
        },
        content = { innerPadding ->
            MainContent(
                sesami = sesami,
                viewSelected = viewSelected,
                goHome = {
                    viewSelected = ViewSelected.LogicalView
                    appBarTitleRID = R.string.app_bar_title_home
                },
                modifier = modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
fun MainContent(
    sesami: NukiSesamiClient,
    viewSelected: ViewSelected,
    goHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(viewSelected) {
        ViewSelected.LogicalView -> LogicalView(sesami, modifier)
        ViewSelected.DetailedStatusView -> DetailedStatusView(sesami, modifier)
        ViewSelected.SettingsView -> SettingsView(sesami, goHome, modifier)
        ViewSelected.AboutView -> AboutView(sesami, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogicalView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    val action = doorActionText(sesami.doorAction)
    val lock = lockStateText(sesami.lockState)
    val door = doorStateText(sesami.doorState)
    var hold by remember { mutableStateOf(true) }

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
                    when(sesami.doorAction) {
                        DoorAction.Close -> sesami.closeDoor()
                        DoorAction.Open -> sesami.openDoor(hold = hold)
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Localized Description")
                Spacer(modifier.padding(end = 5.dp))
                Text(action, fontSize = 36.sp)
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(checked = hold, onCheckedChange = {
                    hold = it
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
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_electric_door_state)) } },
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
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Localized Description"
                        )
                    }
                    Text(door)
                }
            }

            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_smart_lock_state)) } },
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
    goHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mqttHostname by remember { mutableStateOf(sesami.mqttHostname) }
    var mqttPort by remember { mutableStateOf(sesami.mqttPort) }
    var mqttUsername by remember { mutableStateOf(sesami.mqttUsername) }
    var mqttPassword by remember { mutableStateOf(sesami.mqttPassword) }
    var bluetoothAddress by remember { mutableStateOf(sesami.bluetoothAddress) }
    var bluetoothChannel by remember { mutableStateOf(sesami.bluetoothChannel) }
    var changed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = mqttHostname,
            onValueChange = {   // TODO: validate entered value
                                mqttHostname = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_mqtt_hostname)) },
            singleLine = true
        )

        TextField(
            value = mqttPort,
            onValueChange = {   // TODO: validate entered value
                                mqttPort = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_mqtt_port)) },
            singleLine = true
        )

        TextField(
            value = mqttUsername,
            onValueChange = {   // TODO: validate entered value
                                mqttUsername = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_mqtt_username)) },
            singleLine = true
        )

        TextField(
            value = mqttPassword,
            onValueChange = {   // TODO: validate entered value
                                mqttPassword = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_mqtt_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        TextField(
            value = bluetoothAddress,
            onValueChange = {   // TODO: validate entered value
                                bluetoothAddress = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_bluetooth_address)) },
            singleLine = true
        )

        TextField(
            value = bluetoothChannel,
            onValueChange = {   // TODO: validate entered value
                                bluetoothChannel = it
                                changed = true
                            },
            label = { Text(stringResource(R.string.settings_label_bluetooth_channel)) },
            singleLine = true
        )

        Spacer(modifier.padding(10.dp))

        ElevatedButton(
            enabled = changed,
            onClick = {
                // TODO: Save settings

                // Use settings in sesami
                sesami.useSettings(
                    mqttHostname,
                    mqttPort,
                    mqttUsername,
                    mqttPassword,
                    bluetoothAddress,
                    bluetoothChannel,
                )

                // TODO: Notify user of success

                goHome()
            }
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "Localized Description")
            Spacer(modifier.padding(end = 5.dp))
            Text(stringResource(R.string.button_save), fontSize = 20.sp)
        }
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
