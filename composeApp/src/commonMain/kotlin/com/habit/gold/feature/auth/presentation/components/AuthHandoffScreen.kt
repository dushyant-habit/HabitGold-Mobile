package com.habit.gold.feature.auth.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.habit.gold.core.designsystem.AppLoadingView
import com.habit.gold.core.localization.appStrings

@Composable
internal fun AuthHandoffScreen(
    modifier: Modifier = Modifier,
) {
    val strings = appStrings
    AppLoadingView(
        title = strings.authCompletingSignInTitle,
        message = strings.authCompletingSignInMessage,
        modifier = modifier,
    )
}
