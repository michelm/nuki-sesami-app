package com.example.nuki_sesami_app

enum class DoorState(val value: Int) {
    Closed(0),  // Door is closed or about to close
    Opened(1),  // Door is open(ing)
    OpenHold(2), // Door is open(ing) and will be held open
    Unknown(255)
}