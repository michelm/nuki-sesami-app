package com.example.nuki_sesami_app.base

import android.graphics.Bitmap
import com.example.nuki_sesami_app.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class QRConfig(
    preferences: UserPreferences
) {
    private val items: Map<String, Any> = getItems(preferences)

    private fun getItems(preferences: UserPreferences): Map<String, Any> {
        val nukiDeviceID = preferences.load(
            R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID
        )
        val mqttHostname = preferences.load(
            R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
        )
        val mqttPort = preferences.load(
            R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT
        )
        val mqttUsername = preferences.load(
            R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME
        )
        val mqttPassword = preferences.load(
            R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
        )
        val bluetoothAddress = preferences.load(
            R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS
        )
        val bluetoothChannel = preferences.load(
            R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
        )
        val preferBluetooth = preferences.load(
            R.string.preferences_key_prefer_bluetooth, false
        )

        return mapOf(
            "nukiDeviceID" to nukiDeviceID,
            "mqttHostname" to mqttHostname,
            "mqttPort" to mqttPort,
            "mqttUsername" to mqttUsername,
            "mqttPassword" to mqttPassword,
            "bluetoothAddress" to bluetoothAddress,
            "bluetoothChannel" to bluetoothChannel,
            "preferBluetooth" to preferBluetooth,
        )
    }

    fun toJson(): String {
        val json = StringBuilder()
        json.append("{")
        items.entries.forEach { entry ->
            val value = when (entry.value) {
                is String -> "\"${entry.value}\""
                else -> entry.value.toString()
            }
            json.append("\"${entry.key}\": $value,")
        }
        if (json.endsWith(",")) {
            json.deleteCharAt(json.length - 1)
        }
        json.append("}")
        return json.toString()
    }

    fun generateQRCode(width: Int, height: Int): Bitmap {
        val text = this.toJson()
        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H)
        val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y,
                    if (bitMatrix.get(x, y)) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            }
        }

        return bitmap
    }
}
