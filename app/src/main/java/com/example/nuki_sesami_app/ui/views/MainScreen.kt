package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.nuki_sesami_app.R
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.NukiSesamiClient
import com.example.nuki_sesami_app.ui.misc.RequestAppPermissions
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme

@Composable
fun TopAppBarTitle(
    appBarTitleRID: Int
) {
    Text(
        text = stringResource(appBarTitleRID),
        maxLines = 1,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun TopAppBarNavigationIcon(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = "Localized description"
        )
    }
}

@Composable
fun TopAppBarActionIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Localized description"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    preferences: UserPreferences,
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var viewSelected by remember { mutableStateOf(ViewSelected.LogicalView) }
    var appBarTitleRID by remember { mutableIntStateOf(R.string.app_bar_title_home) }
    var settingsChanged by remember { mutableStateOf(false) }
    val openAboutDialog = remember { mutableStateOf(false) }
    val requestAppPermissions = remember { mutableStateOf(true) }
    var sesamiConnected by remember { mutableStateOf(sesami.connected.value) }

    sesami.connected.subscribe {
        sesamiConnected = it
    }

    if (requestAppPermissions.value) {
        RequestAppPermissions { granted ->
            if (granted) {
                sesami.activate()
            } else {
                // TODO: show warning?
            }
        }

        requestAppPermissions.value = false
    }

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
                    title = { TopAppBarTitle(appBarTitleRID) },
                    navigationIcon = { TopAppBarNavigationIcon(onClick = { menuExpanded = true }) },
                    actions = {
                        TopAppBarActionIconButton(
                            onClick = { appBarTitleRID = changeView(ViewSelected.LogicalView,
                                R.string.app_bar_title_home)
                            },
                            imageVector = if (viewSelected == ViewSelected.LogicalView)
                                Icons.Filled.Home else Icons.Outlined.Home
                        )

                        TopAppBarActionIconButton(
                            onClick = { appBarTitleRID = changeView(ViewSelected.DetailedStatusView,
                                R.string.app_bar_title_detailed_status)
                            },
                            imageVector = if (viewSelected == ViewSelected.DetailedStatusView)
                                Icons.Filled.DateRange else Icons.Outlined.DateRange
                        )

                        TopAppBarActionIconButton(
                            onClick = {
                                sesami.deactivate()
                                sesami.activate()
                                      },
                            imageVector = Icons.Outlined.Refresh,
                            enabled = (!sesamiConnected)
                        )

                        TopAppBarActionIconButton(
                            onClick = { appBarTitleRID = changeView(ViewSelected.SettingsView,
                                R.string.app_bar_title_settings)
                            },
                            imageVector = if (viewSelected == ViewSelected.SettingsView)
                                Icons.Filled.Settings else Icons.Outlined.Settings
                        )
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
        ViewSelected.SettingsView -> SettingsView(sesami, modifier, preferences)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val context = LocalContext.current
    NukiSesamiAppTheme {
        MainScreen(
            preferences = UserPreferences(),
            sesami = NukiSesamiClient(context, null, null),
            modifier = Modifier
        )
    }
}
