package com.example.nuki_sesami_app.ui.views

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.ui.misc.SelectedBluetoothDevice

@Composable
fun BluetoothDeviceDialog(
    bluetoothAdapter: BluetoothAdapter?,
    onDismiss: () -> Unit,
    onDeviceSelected: (SelectedBluetoothDevice) -> Unit
) {
    if (ContextCompat.checkSelfPermission(LocalContext.current,
            Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        onDismiss()
    }

    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val devices = pairedDevices?.toList() ?: emptyList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_select_bluetooth_device)) },
        text = {
            if (devices.isNotEmpty()) {
                Column {
                    HorizontalDivider()

                    devices.forEach { device ->
                        Text(
                            text = device.name ?: device.address,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val name = device.name ?: device.address
                                    val selected = SelectedBluetoothDevice(name, device.address)
                                    onDeviceSelected(selected)
                                }
                                .padding(8.dp)
                        )
                    }
                }
            } else {
                Text("No paired devices available") // TODO: localize
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BluetoothDeviceDialogPreview() {
    BluetoothDeviceDialog(
        bluetoothAdapter = null,
        onDismiss = {},
        onDeviceSelected = {}
    )
}
