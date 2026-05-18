package com.habit.gold.feature.rewards.presentation

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
internal actual fun RewardsQrCodePreview(
    referralCode: String,
    modifier: Modifier,
) {
    val bitmap = remember(referralCode) {
        generateRewardsQrCodeBitmap(referralCode)
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "My QR",
            modifier = modifier,
        )
    } else {
        Text(
            text = "QR preview is unavailable right now",
            color = Slate500,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = modifier,
        )
    }
}

private fun generateRewardsQrCodeBitmap(
    referralCode: String,
    sizePx: Int = 768,
): Bitmap? {
    return runCatching {
        val content = referralInviteLink(referralCode)
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888).apply {
            for (x in 0 until matrix.width) {
                for (y in 0 until matrix.height) {
                    setPixel(
                        x,
                        y,
                        if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE,
                    )
                }
            }
        }
    }.getOrNull()
}
