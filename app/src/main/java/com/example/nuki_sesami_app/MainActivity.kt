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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
                    simulation = false,
                    modifier = Modifier
                )
            }
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
fun connectionStateText(connected: Boolean): String {
    return when (connected) {
        true -> stringResource(R.string.connection_state_connected)
        false -> stringResource(R.string.connection_state_not_connected)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    simulation: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val sesami = remember { getSesamiClient(preferences, activate=true, simulation=simulation) }
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
                preferences = preferences,
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
    preferences: UserPreferences,
    sesami: NukiSesamiClient,
    viewSelected: ViewSelected,
    goHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(viewSelected) {
        ViewSelected.LogicalView -> LogicalView(sesami, modifier, preferences)
        ViewSelected.DetailedStatusView -> DetailedStatusView(sesami, modifier)
        ViewSelected.SettingsView -> SettingsView(sesami, modifier, preferences, goHome)
        ViewSelected.AboutView -> AboutView(sesami, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogicalViewDetailsEntry(
    icon: ImageVector,
    text: String,
    tooltip: Int
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
                Icon(
                    imageVector = icon,
                    contentDescription = "Localized Description"
                )
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
    var mqtt by remember { mutableStateOf(sesami.mqttConnected.value) }
    var bluetooth by remember { mutableStateOf(sesami.bluetoothConnected.value) }
    var connected by remember { mutableStateOf(mqtt || bluetooth) }
    var hold by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_switch_openhold, true)) }

    LaunchedEffect(sesami) {
        sesami.doorAction.subscribe { value: DoorAction -> action = value }
        sesami.doorState.subscribe { value: DoorState -> door = value }
        sesami.lockState.subscribe { value: LockState -> lock = value }
        sesami.mqttConnected.subscribe { value: Boolean ->
            mqtt = value
            connected = mqtt || bluetooth
        }
        sesami.bluetoothConnected.subscribe { value: Boolean ->
            bluetooth = value
            connected = mqtt || bluetooth
        }
    }

    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (verticalAlignment = Alignment.CenterVertically
        ){
            ElevatedButton(
                enabled = action != DoorAction.None,
                onClick = {
                    when(action) {
                        DoorAction.None -> { /* no action */  }
                        DoorAction.Close -> sesami.closeDoor()
                        DoorAction.Open -> sesami.openDoor(hold = hold)
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Localized Description")
                Spacer(modifier.padding(end = 5.dp))
                Text(doorActionText(action), fontSize = 36.sp)
            }
        }

        Row (verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                enabled = action == DoorAction.Open,
                checked = hold,
                onCheckedChange = {
                    hold = it
                    preferences.save(R.string.preferences_key_switch_openhold, it)
                }
            )
            Spacer(modifier.padding(end = 10.dp))
            Text(
                stringResource(R.string.door_mode_switch_text),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(modifier.padding(10.dp))

        Column(
            modifier = modifier,
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
                if (mqtt) "mqtt" else { if (bluetooth) "bluetooth" else "---" },
                R.string.tooltip_sesami_connection_state
            )
        }
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
    var mqtt by remember { mutableStateOf(sesami.mqttConnected.value) }
    var mqttError by remember { mutableStateOf("") }
    var bluetooth by remember { mutableStateOf(false) }

    LaunchedEffect(sesami) {
        sesami.doorAction.subscribe { value: DoorAction -> action = value }
        sesami.doorState.subscribe { value: DoorState -> door = value }
        sesami.doorMode.subscribe { value: DoorMode -> mode = value }
        sesami.doorSensor.subscribe { value: DoorSensorState -> sensor = value }
        sesami.lockState.subscribe { value: LockState -> lock = value }
        sesami.mqttConnected.subscribe { value: Boolean -> mqtt = value }
        sesami.mqttError.subscribe { value: String -> mqttError = value }
        sesami.bluetoothConnected.subscribe { value: Boolean -> bluetooth = value }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            DetailedStatusViewEntry(Icons.Filled.CheckCircle,"door action",
                doorActionText(action))

            DetailedStatusViewEntry(Icons.Filled.Home,"door state",
                doorStateText(door))

            DetailedStatusViewEntry(Icons.Filled.Info,"door mode",
                doorModeText(mode))

            DetailedStatusViewEntry(Icons.Filled.Info,"door sensor",
                doorSensorText(sensor))

            DetailedStatusViewEntry(Icons.Filled.Info,"lock state",
                lockStateText(lock))

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

@Composable
fun SettingsView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier,
    preferences: UserPreferences,
    goHome: () -> Unit,
) {
    var nukiDeviceID by remember { mutableStateOf(sesami.nukiDeviceID) }
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
            value = nukiDeviceID,
            onValueChange = {   // TODO: validate entered value
                nukiDeviceID = it
                changed = true
            },
            label = { Text(stringResource(R.string.settings_label_nuki_device_id)) },
            singleLine = true
        )

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
                // Save user preferences
                preferences.save(R.string.preferences_key_nuki_device_id, nukiDeviceID)
                preferences.save(R.string.preferences_key_mqtt_hostname, mqttHostname)
                preferences.save(R.string.preferences_key_mqtt_port, mqttPort)
                preferences.save(R.string.preferences_key_mqtt_username, mqttUsername)
                preferences.save(R.string.preferences_key_mqtt_password, mqttPassword)
                preferences.save(R.string.preferences_key_bluetooth_address, bluetoothAddress)
                preferences.save(R.string.preferences_key_bluetooth_channel, bluetoothChannel)

                // Use updated settings in sesami
                sesami.configure(
                    nukiDeviceID,
                    mqttHostname,
                    mqttPort,
                    mqttUsername,
                    mqttPassword,
                    bluetoothAddress,
                    bluetoothChannel,
                )

                sesami.deactivate()

                sesami.activate()

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
fun AboutViewEntry(caption: String, value: String, modifier: Modifier = Modifier)
{
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${caption}: ")
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AboutView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    val appName = "nuki-sesami"
    val appVersion = BuildConfig.VERSION_NAME
    val buildType = BuildConfig.BUILD_TYPE
    var serverVersion by remember { mutableStateOf(sesami.version.value) }
    val description = stringResource(R.string.about_view_description)

    LaunchedEffect(sesami) {
        sesami.version.subscribe { value: String -> serverVersion = value }
    }

    Column(
        modifier = modifier
            .padding(start=20.dp, end=20.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            Text(stringResource(R.string.about_view_description_caption), fontWeight = FontWeight.Bold)
            HorizontalDivider()
            Text(description)
            HorizontalDivider()
        }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start
        ) {
            AboutViewEntry(stringResource(R.string.about_view_entry_caption_application), appName)
            AboutViewEntry(stringResource(R.string.about_view_entry_caption_version), appVersion)
            AboutViewEntry(stringResource(R.string.about_view_entry_caption_build_type), buildType)
            AboutViewEntry(stringResource(R.string.about_view_entry_caption_server), serverVersion)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NukiSesamiAppTheme {
        MainScreen(
            simulation = true,
            modifier = Modifier
        )
    }
}
