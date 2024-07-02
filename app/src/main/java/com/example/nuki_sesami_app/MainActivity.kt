package com.example.nuki_sesami_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AndroidUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
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

private const val TAG_URL = "ANNOTATION_TAG_URL"

fun attachLink(
    source: String,
    segment: String,
    link: String
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    builder.append(source) // load current text into the builder
    val start = source.indexOf(segment) // start of span marked by 'segment'
    val end = start + segment.length // end of span marked by 'segment'

    builder.addStyle(
        SpanStyle(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
        ),
        start,
        end
    )

    builder.addStringAnnotation(
        TAG_URL, // link can be accessed using this tag
        link,
        start,
        end
    )
    return builder.toAnnotatedString()
}

@Composable
fun AboutDialogEntry(
    caption: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${caption}: ")
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit,
) {
    val version = BuildConfig.VERSION_NAME
    val build = BuildConfig.BUILD_TYPE
    val link = "https://github.com/michelm/nuki-sesami-app"
    val text = stringResource(R.string.about_view_description)
    val annotatedText = attachLink(
        source = text,
        segment = "nuki-sesami-app",
        link = link,
    )
    val uriHandler = AndroidUriHandler(LocalContext.current)

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    stringResource(R.string.about_view_description_caption),
                    style = MaterialTheme.typography.titleLarge,
                )
                HorizontalDivider(thickness = 2.dp)
                ClickableText(
                    modifier = Modifier.padding(1.dp),
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    onClick = {
                        annotatedText
                            .getStringAnnotations(TAG_URL, it, it)
                            .firstOrNull()
                            ?.let { url -> uriHandler.openUri(url.item) }
                    }
                )

                HorizontalDivider(thickness = 2.dp)
                AboutDialogEntry(stringResource(R.string.about_view_entry_caption_version), version)
                AboutDialogEntry(stringResource(R.string.about_view_entry_caption_build_type), build)
                Spacer(Modifier.padding(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Close")
                    }
                }
            }
        }
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
    var settingsChanged by remember { mutableStateOf(false) }
    var snackBarMessage by remember { mutableStateOf("") }
    val openAboutDialog = remember { mutableStateOf(false) }

    val changeView = fun (next: ViewSelected, titleID: Int): Int {
        val current = viewSelected

        if (next == current) {
            return titleID
        }

        if (current == ViewSelected.SettingsView && settingsChanged) {
            // Use updated settings in sesami
            sesami.configure(
                preferences.load(R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID),
                preferences.load(R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME),
                preferences.load(R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT),
                preferences.load(R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME),
                preferences.load(R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD),
                preferences.load(R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS),
                preferences.load(R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL),
            )

            // Enforce sesami to use new settings
            sesami.deactivate()
            sesami.activate()

            // Inform user
            snackBarMessage = getString(context, R.string.snackbar_message_settings_updated)
        }

        viewSelected = next
        settingsChanged = false
        return titleID
    }

    preferences.subscribe { _, _ -> // (key, value) arguments not used
        // Remember settings have changed; use updated settings when leaving view
        settingsChanged = true
    }

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
                            appBarTitleRID = changeView(ViewSelected.LogicalView, R.string.app_bar_title_home)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = {
                            appBarTitleRID = changeView(ViewSelected.DetailedStatusView, R.string.app_bar_title_detailed_status)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Localized description"
                            )
                        }
                        IconButton(onClick = {
                            appBarTitleRID = changeView(ViewSelected.SettingsView, R.string.app_bar_title_settings)
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
                        onClick = {
                            appBarTitleRID = changeView(ViewSelected.LogicalView, R.string.app_bar_title_home)
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_detailed_status)) },
                        onClick = {
                            appBarTitleRID = changeView(ViewSelected.DetailedStatusView, R.string.app_bar_title_detailed_status)
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.DateRange, contentDescription = null) }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_settings)) },
                        onClick = {
                            appBarTitleRID = changeView(ViewSelected.SettingsView, R.string.app_bar_title_settings)
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_about)) },
                        onClick = {
                            openAboutDialog.value = true
                            menuExpanded = false
                        },
                        leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                    )
                }
            }
        },
        content = { innerPadding ->
            Box {
                MainContent(
                    preferences = preferences,
                    sesami = sesami,
                    viewSelected = viewSelected,
                    modifier = modifier.padding(innerPadding)
                )

                when {
                    openAboutDialog.value -> {
                        AboutDialog(
                            onDismissRequest = { openAboutDialog.value = false },
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box {
                Snackbar(
                    action = {
                        IconButton(
                            onClick = { snackBarMessage = "" },
                            enabled = (snackBarMessage.isNotEmpty())
                        ) {
                            Icon(Icons.Outlined.Clear, contentDescription = "Localized description")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (simulation) {
                            Icon(Icons.Outlined.Star, contentDescription = "Localized description")
                            Text(text = "demo   ")
                        }
                        Text(text = snackBarMessage)
                    }
                }
            }
        }
    )
}

@Composable
fun MainContent(
    preferences: UserPreferences,
    sesami: NukiSesamiClient,
    viewSelected: ViewSelected,
    modifier: Modifier = Modifier
) {
    when(viewSelected) {
        ViewSelected.LogicalView -> LogicalView(sesami, modifier, preferences)
        ViewSelected.DetailedStatusView -> DetailedStatusView(sesami, modifier)
        ViewSelected.SettingsView -> SettingsView(modifier, preferences)
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

        HorizontalDivider(modifier = Modifier.padding(20.dp))

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
                    if (mqtt) "mqtt" else { if (bluetooth) "bluetooth" else "---" },
                    R.string.tooltip_sesami_connection_state
                )
            }
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
    var serverVersion by remember { mutableStateOf(sesami.version.value) }
    var mqtt by remember { mutableStateOf(sesami.mqttConnected.value) }
    var mqttError by remember { mutableStateOf("") }
    var bluetooth by remember { mutableStateOf(false) }

    LaunchedEffect(sesami) {
        sesami.doorAction.subscribe { value: DoorAction -> action = value }
        sesami.doorState.subscribe { value: DoorState -> door = value }
        sesami.doorMode.subscribe { value: DoorMode -> mode = value }
        sesami.doorSensor.subscribe { value: DoorSensorState -> sensor = value }
        sesami.lockState.subscribe { value: LockState -> lock = value }
        sesami.version.subscribe { value: String -> serverVersion = value }
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
            DetailedStatusViewEntry(Icons.Filled.CheckCircle,
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

fun isValidNukiDeviceIDArg(arg: String): Boolean {
    try {
        arg.toLong(radix = 16) // throws exception when not a valid hexadecimal
        return true
    }
    catch (e: NumberFormatException) {
        return false
    }
    catch (e: NumberFormatException) {
        return false
    }
}

fun parseMqttPortArg(arg: Int?): Int? {
    if (arg == null || arg < NUKI_SESAMI_MIN_MQTT_PORT || arg > NUKI_SESAMI_MAX_MQTT_PORT) {
        return null
    }

    return arg
}

fun parseBluetoothChannelArg(arg: Int?): Int? {
    if (arg == null || arg < NUKI_SESAMI_MIN_BLUETOOTH_CHANNEL || arg > NUKI_SESAMI_MAX_BLUETOOTH_CHANNEL) {
        return null
    }

    return arg
}

@Composable
fun QRCodeDialog(
    onDismissRequest: () -> Unit,
    preferences: UserPreferences,
) {
    val qrcode = QRConfig(preferences)

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Box {
                val qrCode = qrcode.generateQRCode(400, 400)
                Image(qrCode.asImageBitmap(), "QR code")
            }
        }
    }
}

@Composable
fun SettingsViewQRButton(
    onClick: () -> Unit,
    drawableResID: Int,
    caption: String
) {
    ElevatedButton(
        modifier = Modifier.padding(end = 5.dp),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 3.dp)
                .size(20.dp),
            painter = painterResource(drawableResID),
            contentDescription = "Localized Description"
        )
        Text(caption, fontSize = 18.sp)
    }
}

@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    preferences: UserPreferences,
) {
    var nukiDeviceID by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID)
    ) }

    var mqttHostname by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME)
    ) }

    var mqttPort by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT).toString()
    ) }

    var mqttUsername by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME)
    ) }

    var mqttPassword by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD)
    ) }

    var bluetoothAddress by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS)
    ) }

    var bluetoothChannel by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL).toString()
    ) }

    var validMqttPort by remember { mutableStateOf (true) }
    var validBluetoothChannel by remember { mutableStateOf (true) }
    var validNukiDeviceID by remember { mutableStateOf (true) }
    val openQRCodeDialog = remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsViewQRButton(
                    onClick = { openQRCodeDialog.value = true },
                    drawableResID = R.drawable.qr_code_2_24px,
                    caption = "code"
                )

                SettingsViewQRButton(
                    onClick = {},
                    drawableResID = R.drawable.qr_code_scanner_24px,
                    caption = "scan"
                )
            }

            TextField(
                value = nukiDeviceID,
                onValueChange = {
                    nukiDeviceID = it
                    validNukiDeviceID = isValidNukiDeviceIDArg(it)
                    if (validNukiDeviceID) {
                        preferences.save(R.string.preferences_key_nuki_device_id, nukiDeviceID)
                    }
                },
                label = { Text(stringResource(R.string.settings_label_nuki_device_id)) },
                singleLine = true,
                isError = !validNukiDeviceID
            )

            TextField(
                value = mqttHostname,
                onValueChange = {
                    // TODO: check if valid hostname or IP(6) address?
                    mqttHostname = it
                    preferences.save(R.string.preferences_key_mqtt_hostname, mqttHostname)
                },
                label = { Text(stringResource(R.string.settings_label_mqtt_hostname)) },
                singleLine = true
            )

            TextField(
                value = mqttPort,
                onValueChange = {
                    val port = parseMqttPortArg(it.toIntOrNull())
                    if (port != null) {
                        preferences.save(R.string.preferences_key_mqtt_port, port)
                    }
                    mqttPort = it
                    validMqttPort = (port != null)
                },
                label = { Text(stringResource(R.string.settings_label_mqtt_port)) },
                singleLine = true,
                isError = !validMqttPort
            )

            TextField(
                value = mqttUsername,
                onValueChange = {
                    mqttUsername = it
                    preferences.save(R.string.preferences_key_mqtt_username, mqttUsername)
                },
                label = { Text(stringResource(R.string.settings_label_mqtt_username)) },
                singleLine = true
            )

            TextField(
                value = mqttPassword,
                onValueChange = {
                    mqttPassword = it
                    preferences.save(R.string.preferences_key_mqtt_password, mqttPassword)
                },
                label = { Text(stringResource(R.string.settings_label_mqtt_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            TextField(
                value = bluetoothAddress,
                onValueChange = {
                    // TODO: check if valid bluetooth address
                    bluetoothAddress = it
                    preferences.save(R.string.preferences_key_bluetooth_address, bluetoothAddress)
                },
                label = { Text(stringResource(R.string.settings_label_bluetooth_address)) },
                singleLine = true
            )

            TextField(
                value = bluetoothChannel,
                onValueChange = {
                    val channel = parseBluetoothChannelArg(it.toIntOrNull())
                    if (channel != null) {
                        preferences.save(R.string.preferences_key_bluetooth_channel, channel)
                    }
                    bluetoothChannel = it
                    validBluetoothChannel = (channel != null)
                },
                label = { Text(stringResource(R.string.settings_label_bluetooth_channel)) },
                singleLine = true,
                isError = !validBluetoothChannel
            )
        }

        when {
            openQRCodeDialog.value -> {
                QRCodeDialog(
                    onDismissRequest = { openQRCodeDialog.value = false },
                    preferences = preferences,
                )
            }
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
