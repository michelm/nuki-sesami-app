package com.example.nuki_sesami_app.ui.views

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_DEVICE_ID
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_MQTT_PORT
import com.example.nuki_sesami_app.NUKI_SESAMI_DEFAULT_MQTT_USERNAME
import com.example.nuki_sesami_app.NUKI_SESAMI_MAX_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.NUKI_SESAMI_MAX_MQTT_PORT
import com.example.nuki_sesami_app.NUKI_SESAMI_MIN_BLUETOOTH_CHANNEL
import com.example.nuki_sesami_app.NUKI_SESAMI_MIN_MQTT_PORT
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.UserPreferences

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
fun SettingsViewButton(
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
        R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID
    )
    ) }

    var mqttHostname by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
    )
    ) }

    var mqttPort by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT
    ).toString()
    ) }

    var mqttUsername by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME
    )
    ) }

    var mqttPassword by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
    )
    ) }

    var bluetoothAddress by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS
    )
    ) }

    var bluetoothChannel by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
    ).toString()
    ) }

    var simulation by remember { mutableStateOf(preferences.load(
        R.string.preferences_key_simulation_mode, false)
    ) }
    var preferBluetooth by remember { mutableStateOf (preferences.load(
        R.string.preferences_key_prefer_bluetooth, false)
    ) }

    var validMqttPort by remember { mutableStateOf (true) }
    var validBluetoothAddress by remember { mutableStateOf (true) }
    var validBluetoothChannel by remember { mutableStateOf (true) }
    var validNukiDeviceID by remember { mutableStateOf (true) }
    val openQRCodeDialog = remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsViewButton(
                    onClick = { openQRCodeDialog.value = true },
                    drawableResID = R.drawable.qr_code_2_24px,
                    caption = "code"
                )
                SettingsViewButton(
                    onClick = { /* TODO: open QR scanner and processing logic */ },
                    drawableResID = R.drawable.qr_code_scanner_24px,
                    caption = "scan"
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 5.dp,
                    bottom = 5.dp
                )
            )

            Column(horizontalAlignment = Alignment.Start
            ) {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        modifier = Modifier.padding(end = 5.dp),
                        checked = preferBluetooth,
                        onCheckedChange = {
                            preferBluetooth = it
                            preferences.save(R.string.preferences_key_prefer_bluetooth, it)
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
                if (preferBluetooth) {
                    TextField(
                        value = bluetoothAddress,
                        onValueChange = {
                            validBluetoothAddress = BluetoothAdapter.checkBluetoothAddress(it)
                            bluetoothAddress = it

                            if (validBluetoothAddress) {
                                preferences.save(
                                    R.string.preferences_key_bluetooth_address,
                                    bluetoothAddress
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.settings_label_bluetooth_address)) },
                        singleLine = true,
                        isError = !validBluetoothAddress
                    )

                    TextField(
                        value = bluetoothChannel,
                        onValueChange = {
                            val channel = parseBluetoothChannelArg(it.toIntOrNull())
                            if (channel != null) {
                                preferences.save(
                                    R.string.preferences_key_bluetooth_channel,
                                    channel
                                )
                            }
                            bluetoothChannel = it
                            validBluetoothChannel = (channel != null)
                        },
                        label = { Text(stringResource(R.string.settings_label_bluetooth_channel)) },
                        singleLine = true,
                        isError = !validBluetoothChannel
                    )
                } else {
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
                }
            }
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
fun SettingsViewPreview() {
    SettingsView(
        modifier = Modifier,
        preferences = UserPreferences()
    )
}