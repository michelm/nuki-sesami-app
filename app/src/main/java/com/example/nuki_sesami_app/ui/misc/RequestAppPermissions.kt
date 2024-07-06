package com.example.nuki_sesami_app.ui.misc

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

val REQUIRED_APP_PERMISSIONS = arrayOf(
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.ACCESS_NETWORK_STATE,
    android.Manifest.permission.BLUETOOTH,
    android.Manifest.permission.BLUETOOTH_ADMIN,
    android.Manifest.permission.BLUETOOTH_CONNECT,
    android.Manifest.permission.BLUETOOTH_SCAN,
)

fun hasAppPermissions(context: Context): Boolean {
    return REQUIRED_APP_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun RequestAppPermissions(onPermissionsResult: (Boolean) -> Unit) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        onPermissionsResult(allGranted)
    }

    LaunchedEffect(Unit) {
        if (!hasAppPermissions(context)) {
            permissionLauncher.launch(REQUIRED_APP_PERMISSIONS)
        } else {
            onPermissionsResult(true)
        }
    }
}