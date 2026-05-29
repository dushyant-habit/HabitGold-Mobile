package com.habit.gold.core.designsystem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitGoldPullToRefreshIndicator(
    isRefreshing: Boolean,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
) {
    PullToRefreshDefaults.Indicator(
        modifier = modifier,
        isRefreshing = isRefreshing,
        state = state,
        containerColor = Color.White,
        color = HabitGoldPalette.plum,
    )
}
