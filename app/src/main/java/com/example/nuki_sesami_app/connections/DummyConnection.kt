package com.example.nuki_sesami_app.connections

import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorRequestState
import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DummyConnection(
    private val coroutineScope: CoroutineScope?,
    private val nukiDeviceID: String,
): NukiSesamiConnection() {
    private var state = DoorState.Unknown
    private var worker: Job? = null

    init {
        notify("sesami/${nukiDeviceID}/version", "0.0.0")
        notify("sesami/${nukiDeviceID}/state", state.value)
        notify("sesami/${nukiDeviceID}/mode", DoorMode.Unknown.value)
        notify("nuki/${nukiDeviceID}/state", LockState.Undefined.value)
        notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.Unknown.value)
        connected.value = false
        error.value = "Oooeps I did it again!"

        worker = coroutineScope?.launch {
            delay(3000)
            connected.value = true
            error.value = ""
            state = DoorState.Closed
            notify("sesami/${nukiDeviceID}/version", "9.9.9")
            notify("sesami/${nukiDeviceID}/state", state.value)
            notify("sesami/${nukiDeviceID}/mode", DoorMode.OpenClose.value)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlocked.value)
            notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.DoorClosed.value)
        }
    }

    override fun close() {
        worker?.cancel()
    }

    override fun publish(topic: String, value: String) {
        if (!connected.value || topic != "sesami/${nukiDeviceID}/request/state") {
            return
        }

        val request: DoorRequestState = DoorRequestState.from(value)

        when(state) {
            DoorState.Closed -> {
                when (request) {
                    DoorRequestState.Open -> {
                        worker = createOpenDoorWorker()
                    }
                    DoorRequestState.OpenHold -> {
                        worker = createOpenHoldDoorWorker()
                    }
                    else -> { /* ignore the request */ }
                }
            }
            DoorState.OpenHold -> {
                when(request) {
                    DoorRequestState.Close -> {
                        worker = createCloseDoorWorker()
                    }
                    else -> { /* ignore the request */ }
               }
           }
           else -> { /* ignore the request */ }
       }
   }

    private fun createCloseDoorWorker(): Job? {
        return coroutineScope?.launch {
            notify("sesami/${nukiDeviceID}/mode", DoorMode.OpenClose.value)
            delay(500) // delay before closing
            state = DoorState.Closed
            notify("sesami/${nukiDeviceID}/state", state.value)
            delay(3000) // door closes
            notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.DoorClosed.value)
        }
    }

    private fun createOpenDoorWorker(): Job? {
        return coroutineScope?.launch {
            state = DoorState.Opened
            notify("sesami/${nukiDeviceID}/state", state.value)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlatching.value)
            delay(3000)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlatched.value)
            delay(500) // delay between door opening and sensor detecting it
            notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.DoorOpened.value)
            delay(3000)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlocked.value)
            delay(12000)
            notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.DoorClosed.value)
            state = DoorState.Closed
            notify("sesami/${nukiDeviceID}/state", state.value)
        }
    }

    private fun createOpenHoldDoorWorker(): Job? {
        return coroutineScope?.launch {
            state = DoorState.OpenHold
            notify("sesami/${nukiDeviceID}/state", state.value)
            notify("sesami/${nukiDeviceID}/mode", DoorMode.OpenHold.value)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlatching.value)
            delay(3000)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlatched.value)
            delay(500) // door opens
            notify("nuki/${nukiDeviceID}/doorsensorState", DoorSensorState.DoorOpened.value)
            delay(3000)
            notify("nuki/${nukiDeviceID}/state", LockState.Unlocked.value)
            // door is now in open-hold mode
        }
    }
}
