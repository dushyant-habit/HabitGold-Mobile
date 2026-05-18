package com.habit.gold.feature.rewards.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberRewardsClipboard(): RewardsClipboard {
    val context = LocalContext.current
    return remember(context) { AndroidRewardsClipboard(context) }
}

private class AndroidRewardsClipboard(
    context: Context,
) : RewardsClipboard {
    private val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun copy(text: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Referral code", text))
    }
}
