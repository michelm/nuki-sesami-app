package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.nuki_sesami_app.NukiSesamiClient
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_BLUETOOTH_DEVICE
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_DEVICE_ID
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_PORT
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEFAULT_MQTT_USERNAME
import com.example.nuki_sesami_app.base.NUKI_SESAMI_MAX_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.base.NUKI_SESAMI_MAX_MQTT_PORT
import com.example.nuki_sesami_app.base.NUKI_SESAMI_MIN_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.base.NUKI_SESAMI_MIN_MQTT_PORT
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.base.UserPreferences

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
fun SettingsViewSelectSimulation(
    preferences: UserPreferences,
) {
    var simulation by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_simulation_mode, false)
    ) }

    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            modifier = Modifier.padding(end = 5.dp),
            checked = simulation,
            onCheckedChange = {
                simulation = it
                preferences.save(R.string.preferences_key_simulation_mode, it)
            }
        )
        Text(
            "demo",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SettingsViewSelectConnectionType(
    preferences: UserPreferences,
    onPreferBluetooth: (Boolean) -> Unit
) {
    var preferBluetooth by remember { mutableStateOf (preferences.load(
        R.string.preferences_key_prefer_bluetooth, false)
    ) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            modifier = Modifier.padding(end = 5.dp),
            checked = preferBluetooth,
            onCheckedChange = {
                preferBluetooth = it
                preferences.save(R.string.preferences_key_prefer_bluetooth, it)
                onPreferBluetooth(it)
            }
        )
        Text(
            "mqtt",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = if (preferBluetooth) FontWeight.Normal else FontWeight.Bold
        )
        Text(
            " | ",
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "bluetooth",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = if (preferBluetooth) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SettingsViewNukiDeviceID(
    preferences: UserPreferences,
){
    var nukiDeviceID by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID
    ) ) }
    var validNukiDeviceID by remember { mutableStateOf (true) }

    TextField(
        value = nukiDeviceID,
        onValueChange = {
            nukiDeviceID = it
            validNukiDeviceID = isValidNukiDeviceIDArg(it)
            if (validNukiDeviceID) {
                preferences.save(
                    R.string.preferences_key_nuki_device_id,
                    nukiDeviceID
                )
            }
        },
        label = { Text(stringResource(R.string.settings_label_nuki_device_id)) },
        singleLine = true,
        isError = !validNukiDeviceID,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
}

@Composable
fun SettingsViewMqtt(
    preferences: UserPreferences,
) {
    var mqttHostname by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
    ) ) }
    var mqttPort by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT
    ).toString() ) }
    var mqttUsername by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME
    ) ) }
    var mqttPassword by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
    ) ) }
    var validMqttPort by remember { mutableStateOf (true) }

    TextField(
        value = mqttHostname,
        onValueChange = {
            mqttHostname = it
            preferences.save(R.string.preferences_key_mqtt_hostname, mqttHostname)
        },
        label = { Text(stringResource(R.string.settings_label_mqtt_hostname)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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
        isError = !validMqttPort,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )

    TextField(
        value = mqttUsername,
        onValueChange = {
            mqttUsername = it
            preferences.save(R.string.preferences_key_mqtt_username, mqttUsername)
        },
        label = { Text(stringResource(R.string.settings_label_mqtt_username)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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
}

@Composable
fun SettingsViewBluetooth(
    preferences: UserPreferences,
    showDeviceDialog: () -> Unit,
    deviceDialogVisible: Boolean
) {
    var device by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_device, NUKI_SESAMI_DEFAULT_BLUETOOTH_DEVICE
    ) ) }
    var channel by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
    ).toString()) }
    var validChannel by remember { mutableStateOf (true) }
    val prefKeyBluetoothDevice = stringResource(R.string.preferences_key_bluetooth_device)
    val localFocusManager = LocalFocusManager.current

    preferences.subscribe { key, value ->
        if (key == prefKeyBluetoothDevice)
            device = value.toString()
    }

    TextField(
        value = device,
        modifier = Modifier.onFocusChanged {
            if (it.isFocused) {
                showDeviceDialog()

                if (!deviceDialogVisible) {
                    localFocusManager.moveFocus(FocusDirection.Next)
                }
            }
        },
        onValueChange = { device = it },
        label = { Text(stringResource(R.string.settings_label_bluetooth_device)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(

        ),
        readOnly = true
    )

    TextField(
        value = channel,
        onValueChange = {
            val num = parseBluetoothChannelArg(it.toIntOrNull())
            if (num != null) {
                preferences.save(
                    R.string.preferences_key_bluetooth_channel,
                    num
                )
            }
            channel = it
            validChannel = (num != null)
        },
        label = { Text(stringResource(R.string.settings_label_bluetooth_channel)) },
        singleLine = true,
        isError = !validChannel
    )
}

@Composable
fun SettingsView(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier,
    preferences: UserPreferences,
) {
    var preferBluetooth by remember { mutableStateOf (preferences.load(
        R.string.preferences_key_prefer_bluetooth, false)
    ) }
    var showBluetoothDeviceDialog by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.Start
            ) {
                SettingsViewSelectSimulation(
                    preferences
                )

                SettingsViewSelectConnectionType(
                    preferences,
                    onPreferBluetooth = { preferBluetooth = it }
                )

                SettingsViewNukiDeviceID(
                    preferences
                )

                if (preferBluetooth) {
                    SettingsViewBluetooth(
                        preferences = preferences,
                        showDeviceDialog = { showBluetoothDeviceDialog = true },
                        deviceDialogVisible = showBluetoothDeviceDialog
                    )
                } else {
                    SettingsViewMqtt(
                        preferences = preferences,
                    )
                }
            }
        }

        when {
            showBluetoothDeviceDialog -> {
                BluetoothDeviceDialog(
                    bluetoothAdapter = sesami.bluetoothAdapter,
                    onDismiss = { showBluetoothDeviceDialog = false },
                    onDeviceSelected = {
                        showBluetoothDeviceDialog = false
                        preferences.save(R.string.preferences_key_bluetooth_address, it.address)
                        preferences.save(R.string.preferences_key_bluetooth_device, it.name)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsViewPreview() {
    val context = LocalContext.current
    SettingsView(
        sesami = NukiSesamiClient(context, null, null),
        modifier = Modifier,
        preferences = UserPreferences(),
    )
}