package com.example.nuki_sesami_app.ui.misc

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

fun requiredAppPermissions(): Array<String> {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
        )
    } else {
        arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.BLUETOOTH,
        )
    }
}

fun hasAppPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun RequestAppPermissions(onPermissionsResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val requiredPermissions = requiredAppPermissions()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        onPermissionsResult(allGranted)
    }

    LaunchedEffect(Unit) {
        if (!hasAppPermissions(context, requiredPermissions)) {
            permissionLauncher.launch(requiredPermissions)
        } else {
            onPermissionsResult(true)
        }
    }
}