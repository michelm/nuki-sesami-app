package com.example.nuki_sesami_app

import android.content.Context
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.random.Random
import com.example.nuki_sesami_app.state.ConnectionType
import com.example.nuki_sesami_app.state.DoorAction
import com.example.nuki_sesami_app.state.DoorMode
import com.example.nuki_sesami_app.state.DoorSensorState
import com.example.nuki_sesami_app.state.DoorState
import com.example.nuki_sesami_app.state.LockState

class NukiSesamiSimulation: NukiSesamiClient(
) {
    /** Simulation timer used to mimic to some dummy behavior */
    private var simulationTimer: Timer? = null

    init {
        doorMode.value = DoorMode.OpenClose
        doorState.value = DoorState.Closed
        doorSensor.value = DoorSensorState.DoorClosed
        doorAction.value = DoorAction.Open
        lockState.value = LockState.Unlocked
        version.value = "1.2.3"
    }

    override fun simulated(): Boolean { return true }

    override fun openDoor(hold: Boolean) {
        doorMode.value = if (hold) DoorMode.OpenHold else DoorMode.OpenClose
        doorState.value = DoorState.Opened
        doorAction.value = DoorAction.Close
        lockState.value = LockState.Unlatched
        doorSensor.value = DoorSensorState.DoorOpened
    }

    override fun closeDoor() {
        doorMode.value = DoorMode.OpenClose
        doorState.value = DoorState.Closed
        doorAction.value = DoorAction.Open
        lockState.value = LockState.Unlocked
        doorSensor.value = DoorSensorState.DoorClosed
    }

    // Simulate start connection to MQTT / Bluetooth
    override fun activate(context: Context) {
        if (this.simulationTimer != null) {
            return
        }

        this.simulationTimer = timer(
            name = "NukiSesamiSimulationTimer",
            daemon = false,
            initialDelay = 0,
            period = 1000,
            action = {
                doorState.value = DoorState.from(Random.nextInt(1,3))
                doorMode.value = DoorMode.from(Random.nextInt(0,2))
                lockState.value = LockState.from(Random.nextInt(1, 8))
                doorSensor.value = DoorSensorState.from(Random.nextInt(1, 6))
                val n = Random.nextInt(0, 10)
                version.value = "$n.$n.$n"
                connected.value = Random.nextBoolean()
                connectionType.value = ConnectionType.from(Random.nextInt(0,2))
            }
        )
    }

    // Simulate terminate connection with MQTT / Bluetooth
    override fun deactivate() {
        this.simulationTimer?.cancel()
        this.simulationTimer?.purge()
        this.simulationTimer = null
        this.lockState.value = LockState.Undefined
    }
}
