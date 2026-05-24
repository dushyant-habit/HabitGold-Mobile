package com.habit.gold.feature.rewards.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.UIKitView
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.rewards_qr_unavailable
import org.jetbrains.compose.resources.stringResource
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIColor
import platform.UIKit.UIViewContentMode
import qrcode.QRCode

@Composable
internal actual fun RewardsQrCodePreview(
    referralCode: String,
    modifier: Modifier,
) {
    val qrImage = remember(referralCode) {
        generateRewardsQrCodeImage(referralCode)
    }
    if (qrImage != null) {
        UIKitView(
            factory = {
                UIImageView().apply {
                    contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                    image = qrImage
                    clipsToBounds = true
                    backgroundColor = UIColor.whiteColor
                }
            },
            modifier = modifier,
            update = { imageView ->
                imageView.image = qrImage
                imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
                imageView.backgroundColor = UIColor.whiteColor
            },
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

private fun generateRewardsQrCodeImage(referralCode: String): UIImage? {
    return runCatching {
        val content = referralInviteLink(referralCode)
        val qrGraphics = QRCode.ofSquares()
            .withSize(14)
            .withColor(0xFF000000.toInt())
            .withBackgroundColor(0xFFFFFFFF.toInt())
            .build(content)
            .render()
        val nativeImage = qrGraphics.nativeImage()
        nativeImage as? UIImage ?: run {
            println("Rewards QR generation failed: native image type=${nativeImage::class}")
            null
        }
    }.getOrElse { throwable ->
        println("Rewards QR generation failed: ${throwable::class} ${throwable.message ?: "unknown"}")
        null
    }
}
