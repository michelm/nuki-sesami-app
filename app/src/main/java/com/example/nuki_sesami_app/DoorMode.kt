package com.example.nuki_sesami_app

enum class DoorMode(val value: Int) {
    OpenClose(0),  // Door is open for a brief moment, the actual time is defined by the
                        // ERREKA 'Smart Evolution' electric door controller
    OpenHold(1),    // Door will be held open until the push button is pressed again
    Unknown(255)
}