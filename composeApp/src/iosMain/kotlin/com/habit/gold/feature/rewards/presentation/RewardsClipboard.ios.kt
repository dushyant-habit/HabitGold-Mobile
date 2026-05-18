package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIPasteboard

@Composable
internal actual fun rememberRewardsClipboard(): RewardsClipboard {
    return remember { IosRewardsClipboard() }
}

private class IosRewardsClipboard : RewardsClipboard {
    override fun copy(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
