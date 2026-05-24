package com.habit.gold.feature.rewards.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.refer_earn_screen_my_qr
import habitgoldmobile.composeapp.generated.resources.rewards_qr_unavailable
import org.jetbrains.compose.resources.stringResource
import qrcode.QRCode

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
            contentDescription = stringResource(Res.string.refer_earn_screen_my_qr),
            modifier = modifier,
        )
    } else {
        Text(
            text = stringResource(Res.string.rewards_qr_unavailable),
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
        val pngBytes = QRCode.ofSquares()
            .withCanvasSize(sizePx)
            .withColor(0xFF000000.toInt())
            .withBackgroundColor(0xFFFFFFFF.toInt())
            .build(content)
            .renderToBytes(format = "png")
        BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.size)
    }.getOrElse { null }
}
