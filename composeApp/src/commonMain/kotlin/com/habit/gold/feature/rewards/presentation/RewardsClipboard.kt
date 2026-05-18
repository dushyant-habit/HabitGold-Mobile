package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable

internal interface RewardsClipboard {
    fun copy(text: String)
}

@Composable
internal expect fun rememberRewardsClipboard(): RewardsClipboard
