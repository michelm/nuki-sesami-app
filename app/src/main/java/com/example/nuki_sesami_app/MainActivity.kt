package com.example.nuki_sesami_app

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            preferences = UserPreferences(context)
            sesami = NukiSesamiClient()
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
