package com.example.nuki_sesami_app.jsonrpc

/**
 * Encoded message example:
 *      {
 *          "jsonrpc": "2.0",
 *          "method": "set",
 *          "params": {
 *              "door_request_state": 2,
 *          }
 *      }
 */

data class DoorRequestMessage(
    var jsonrpc: String = "2.0",
    var method: String = "set",
    var params: DoorRequestParams,
)
