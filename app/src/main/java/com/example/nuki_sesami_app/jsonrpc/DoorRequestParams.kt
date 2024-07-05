package com.example.nuki_sesami_app.jsonrpc

import com.google.gson.annotations.SerializedName

data class DoorRequestParams(
    @SerializedName("door_request_state")
    var state: Int,
)
