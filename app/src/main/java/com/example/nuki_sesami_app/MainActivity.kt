package com.example.nuki_sesami_app

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme
import com.example.nuki_sesami_app.ui.views.MainScreen

class MainActivity : ComponentActivity() {
    private lateinit var preferences: UserPreferences
    private lateinit var sesami: NukiSesamiClient
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = manager.adapter
            preferences = UserPreferences(context)

            // TODO: bluetooth not always working; when unable to connect to
            //  the paired bluetooth device it will block and crash the app
            //  temporary bypass is to force the app to not use bluetooth on startup:
            //preferences.save(R.string.preferences_key_simulation_mode, true)
            preferences.save(R.string.preferences_key_prefer_bluetooth, false)

            sesami = NukiSesamiClient()
            sesami.configure(preferences)

            NukiSesamiAppTheme {
                MainScreen(
                    preferences = preferences,
                    sesami = sesami,
                    bluetoothAdapter = bluetoothAdapter,
                    modifier = Modifier
                )
            }
        }
    }
}
