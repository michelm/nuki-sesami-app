package com.example.nuki_sesami_app

enum class DoorRequestState(val value: Int) {
    None(0),    // No request
    Close(1),   // Close the door
    Open(2),    // Open the door briefly and then close it
    OpenHold(3) // Open the door and hold it open
}
