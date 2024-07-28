package com.example.nuki_sesami_app

import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.base.NUKI_SESAMI_DEMO_ENABLED
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme
import com.example.nuki_sesami_app.ui.views.MainScreen

class MainActivity : ComponentActivity() {
    private lateinit var preferences: UserPreferences
    private lateinit var sesami: NukiSesamiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val coroutineScope = rememberCoroutineScope()

            preferences = UserPreferences(context)

            if (!NUKI_SESAMI_DEMO_ENABLED) {
                preferences.save(R.string.preferences_key_simulation_mode, false)
            }

            sesami = NukiSesamiClient(context, manager.adapter, coroutineScope)
            // REMARK uncomment to force simulation, bluetooth or mqtt mode:
            // preferences.save(R.string.preferences_key_simulation_mode, true)
            // preferences.save(R.string.preferences_key_prefer_bluetooth, false)
            sesami.configure(preferences)

            NukiSesamiAppTheme {
                MainScreen(
                    preferences = preferences,
                    sesami = sesami,
                    modifier = Modifier
                )
            }
        }
    }
}
