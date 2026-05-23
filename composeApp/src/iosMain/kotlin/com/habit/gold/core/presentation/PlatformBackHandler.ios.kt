package com.habit.gold.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    val latestOnBack by rememberUpdatedState(onBack)
    val token = remember {
        IosBackGestureDispatcher.register(
            enabled = enabled,
            onBack = latestOnBack,
        )
    }

    SideEffect {
        IosBackGestureDispatcher.update(
            token = token,
            enabled = enabled,
            onBack = latestOnBack,
        )
    }

    DisposableEffect(token) {
        onDispose {
            IosBackGestureDispatcher.unregister(token)
        }
    }
}
