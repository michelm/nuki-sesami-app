package com.example.nuki_sesami_app.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.nuki_sesami_app.base.UserPreferences
import com.example.nuki_sesami_app.base.QRConfig

@Composable
fun QRCodeDialog(
    onDismissRequest: () -> Unit,
    preferences: UserPreferences,
) {
    val qrcode = QRConfig(preferences)
    var pixels: Int

    LocalDensity.current.run {
        pixels = 400.dp.toPx().toInt()
    }

    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(10.dp),
        ) {
            Box {
                val qrCode = qrcode.generateQRCode(pixels, pixels)
                Image(qrCode.asImageBitmap(), "QR code")
            }
        }
    }
}
