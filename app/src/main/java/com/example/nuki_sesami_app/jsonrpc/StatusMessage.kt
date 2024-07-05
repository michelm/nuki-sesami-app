package com.example.nuki_sesami_app.jsonrpc

/**
 * Encoded message example:
 *      {
 *          "jsonrpc": "2.0",
 *          "method": "status",
 *          "params": {
 *              "nuki": {"lock": 3, "doorsensor": 2},
 *              "door": {"state": 0, "mode": 0},
 *              "relay": {"openclose": true, "openhold": false, "opendoor": false},
 *              "version": "2.1.1"
 *          }
 *      }
 */

data class StatusMessage(
    var jsonrpc: String,
    var method: String,
    var params: StatusParams,
)
