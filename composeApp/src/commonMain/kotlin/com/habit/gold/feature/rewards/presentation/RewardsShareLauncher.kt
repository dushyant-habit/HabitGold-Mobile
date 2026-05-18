package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable

internal interface RewardsShareLauncher {
    fun launch(text: String)
}

@Composable
internal expect fun rememberRewardsShareLauncher(): RewardsShareLauncher
