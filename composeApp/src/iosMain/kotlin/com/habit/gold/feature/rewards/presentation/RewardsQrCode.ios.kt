package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGAffineTransformMakeScale
import platform.CoreImage.CIContext
import platform.CoreImage.CIFilter
import platform.CoreImage.CIQRCodeGeneratorProtocol
import platform.CoreImage.QRCodeGenerator
import platform.CoreImage.createCGImage
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

@Composable
@OptIn(ExperimentalForeignApi::class)
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
                }
            },
            modifier = modifier,
            update = { imageView ->
                imageView.image = qrImage
                imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit
            },
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

@OptIn(ExperimentalForeignApi::class)
private fun generateRewardsQrCodeImage(
    referralCode: String,
    sizePx: Int = 768,
): UIImage? {
    return runCatching {
        val content = referralInviteLink(referralCode)
        val filter = CIFilter.Companion.QRCodeGenerator() as? CIQRCodeGeneratorProtocol ?: return null
        filter.message = content.encodeToByteArray().toNSData()
        filter.correctionLevel = "M"

        val outputImage = filter.outputImage ?: return null
        val extent = outputImage.extent
        val scaleX = sizePx.toDouble() / extent.useContents { size.width }
        val scaleY = sizePx.toDouble() / extent.useContents { size.height }
        val scaledImage = outputImage.imageByApplyingTransform(
            CGAffineTransformMakeScale(scaleX, scaleY),
        )

        val context = CIContext()
        val cgImage = context.createCGImage(scaledImage, fromRect = scaledImage.extent) ?: return null
        UIImage.imageWithCGImage(cgImage)
    }.getOrNull()
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    return usePinned { pinned ->
        NSData.Companion.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}
