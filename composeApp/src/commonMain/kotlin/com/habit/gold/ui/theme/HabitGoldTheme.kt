package com.habit.gold.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun HabitGoldTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    com.habit.gold.core.designsystem.HabitGoldTheme(
        useDarkTheme = useDarkTheme,
        content = content,
    )
}
