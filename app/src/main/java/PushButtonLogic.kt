package com.example.nuki_sesami_app

enum class PushButtonLogic(val value: Int) {
    OpenHold(0),    // Default logic
    Open(1),        // Push to open (briefly) and auto close after a few seconds
    Toggle(2)       // Toggle between 'Open' and 'OpenHold' door modes
}
