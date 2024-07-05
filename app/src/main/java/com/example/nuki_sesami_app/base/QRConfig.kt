package com.example.nuki_sesami_app.base

import android.graphics.Bitmap
import com.example.nuki_sesami_app.R
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class QRConfig(
    preferences: UserPreferences
) {
    private val data = QRConfigData(
        nukiDeviceID = preferences.load(
            R.string.preferences_key_nuki_device_id, NUKI_SESAMI_DEFAULT_DEVICE_ID
        ),
        mqttHostname = preferences.load(
            R.string.preferences_key_mqtt_hostname, NUKI_SESAMI_DEFAULT_MQTT_HOSTNAME
        ),
        mqttPort = preferences.load(
            R.string.preferences_key_mqtt_port, NUKI_SESAMI_DEFAULT_MQTT_PORT
        ),
        mqttUsername = preferences.load(
            R.string.preferences_key_mqtt_username, NUKI_SESAMI_DEFAULT_MQTT_USERNAME
        ),
        mqttPassword = preferences.load(
            R.string.preferences_key_mqtt_password, NUKI_SESAMI_DEFAULT_MQTT_PASSWORD
        ),
        bluetoothAddress = preferences.load(
            R.string.preferences_key_bluetooth_address, NUKI_SESAMI_DEFAULT_BLUETOOTH_ADDRESS
        ),
        bluetoothChannel = preferences.load(
            R.string.preferences_key_bluetooth_channel, NUKI_SESAMI_DEFAULT_BLUETOOTH_CHANNEL
        ),
        preferBluetooth = preferences.load(
            R.string.preferences_key_prefer_bluetooth, false
        )
    )

    fun generateQRCode(width: Int, height: Int): Bitmap {
        val text = Gson().toJson(data)
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
