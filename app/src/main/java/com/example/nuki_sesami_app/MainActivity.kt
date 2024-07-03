package com.example.nuki_sesami_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var preferences: UserPreferences
    private lateinit var sesami: NukiSesamiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            preferences = getUserPreferences()
            sesami = getSesamiClient(
                simulation = false // TODO: get from preferences?
            )

            NukiSesamiAppTheme {
                MainScreen(
                    preferences = preferences,
                    sesami = sesami,
                    modifier = Modifier
                )
            }

            sesami.configure(preferences)
            sesami.activate()
        }
    }
}

@Composable
fun getUserPreferences(): UserPreferences {
    return UserPreferences(LocalContext.current)
}

@Composable
fun getSesamiClient(simulation: Boolean): NukiSesamiClient {
    val context = LocalContext.current

    return if (simulation)
        NukiSesamiClientSimulation(context)
    else
        NukiSesamiClient(context)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    preferences: UserPreferences,
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
            sesami.configure(preferences)

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
                        if (sesami.simulated()) {
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val preferences: UserPreferences = getUserPreferences()
    val sesami: NukiSesamiClient = getSesamiClient(
        simulation = true
    )

    NukiSesamiAppTheme {
        MainScreen(
            preferences = preferences,
            sesami = sesami,
            modifier = Modifier
        )
    }

    sesami.configure(preferences)
    sesami.activate()
}
