package com.example.nuki_sesami_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nuki_sesami_app.ui.theme.NukiSesamiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NukiSesamiAppTheme {
                Scaffold (modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(
                        sesami = NukiSesamiClient(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun doorActionText(action: DoorAction): String {
    return when (action) {
        DoorAction.Open -> stringResource(R.string.door_action_open)
        else -> stringResource(R.string.door_action_close)
    }
}

@Composable
fun doorStateText(state: DoorState): String {
    return when (state) {
        DoorState.OpenHold -> stringResource(R.string.door_state_openhold)
        DoorState.Opened -> stringResource(R.string.door_state_opened)
        DoorState.Closed -> stringResource(R.string.door_state_closed)
        DoorState.Undefined -> stringResource(R.string.door_state_undefined)
    }
}


@Composable
fun doorModeText(mode: DoorMode): String {
    return when(mode) {
        DoorMode.OpenHold -> stringResource(R.string.door_mode_openhold)
        DoorMode.OpenClose -> stringResource(R.string.door_mode_open_close)
        DoorMode.Undefined -> stringResource(R.string.door_mode_undefined)
    }
}

@Composable
fun lockStateText(lockState: LockState): String {
    return when(lockState) {
        LockState.Uncalibrated -> stringResource(R.string.lock_state_uncalibrated)
        LockState.Locked  -> stringResource(R.string.lock_state_Locked)
        LockState.Unlocking  -> stringResource(R.string.lock_state_unlocking)
        LockState.Unlocked -> stringResource(R.string.lock_state_unlocked)
        LockState.Locking -> stringResource(R.string.lock_state_locking)
        LockState.Unlatched -> stringResource(R.string.lock_state_unlatched)
        LockState.Unlocked2 -> stringResource(R.string.lock_state_unlocked2)
        LockState.Unlatching -> stringResource(R.string.lock_state_unlatching)
        LockState.BootRun -> stringResource(R.string.lock_state_boot_run)
        LockState.MotorBlocked -> stringResource(R.string.lock_state_motor_blocked)
        else -> stringResource(R.string.lock_state_undefined)
    }
}

@Composable
fun MainScreen(
    sesami: NukiSesamiClient,
    modifier: Modifier = Modifier
) {
    val action = doorActionText(sesami.doorAction)
    val lock = lockStateText(sesami.lockState)
    val door = doorStateText(sesami.doorState)
    var checked by remember { mutableStateOf(true) }

    Column (
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {},
            modifier = modifier
                .padding(6.dp)
        ) {
            Row {
                Image(
                    painterResource(R.drawable.key_24px),
                    "key",
                    contentScale = ContentScale.Fit,
                    modifier = modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.CenterVertically)
                        .size(48.dp)
                )
                Text(
                    text = action,
                    fontSize = 36.sp,
                    modifier = modifier
                        .align(alignment = Alignment.CenterVertically)
                )
            }
        }

        Column {
            Row(
                modifier = modifier.padding(6.dp)
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                    modifier = modifier
                        .align(alignment = Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(R.string.door_mode_switch_text),
                    modifier = modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(6.dp)
                )
            }

            Row {
                Image(
                    painterResource(R.drawable.door_open_24px),
                    "door status",
                    modifier = modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.CenterVertically)
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = door,
                    modifier = modifier
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Row {
                Image(
                    painterResource(R.drawable.lock_open_24px),
                    "lock status",
                    modifier = modifier
                        .padding(6.dp)
                        .align(alignment = Alignment.CenterVertically)
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = lock,
                    modifier = modifier
                        .align(alignment = Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NukiSesamiAppTheme {
        MainScreen(
            sesami = NukiSesamiClient(),
        )
    }
}
