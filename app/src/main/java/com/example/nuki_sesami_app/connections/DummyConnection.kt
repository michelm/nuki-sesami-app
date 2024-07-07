package com.example.nuki_sesami_app.connections

import java.util.Timer
import kotlin.concurrent.timer
import kotlin.random.Random

class DummyConnection(
    private val nukiDeviceID: String,
): NukiSesamiConnection() {
    /** Simulation timer used to mimic to some dummy behavior */
    private var _timer: Timer? = null

    init {
        _timer = timer(
            name = "DummyConnectionTimer",
            daemon = false,
            initialDelay = 0,
            period = 1000,
            action = {
                var n = Random.nextInt(0, 10)
                notify("sesami/${nukiDeviceID}/version", "$n.$n.$n")

                n = Random.nextInt(1,3)
                notify("sesami/${nukiDeviceID}/state", n.toString())

                n = Random.nextInt(0,2)
                notify("sesami/${nukiDeviceID}/mode", n.toString())

                n = Random.nextInt(1, 8)
                notify("nuki/${nukiDeviceID}/state", n.toString())

                n = Random.nextInt(1, 6)
                notify("nuki/${nukiDeviceID}/doorsensorState", n.toString())

                val c = Random.nextBoolean()
                connected.value = c
                error.value = if (c) "Ok" else "Oeps I did it again!"
            }
        )
    }

    override fun close() {
        _timer?.cancel()
        _timer?.purge()
        _timer = null
    }

    override fun publish(topic: String, value: String) {
        // TODO: implement me
    }
}