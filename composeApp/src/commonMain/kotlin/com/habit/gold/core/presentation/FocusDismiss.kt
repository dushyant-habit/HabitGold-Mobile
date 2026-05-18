package com.habit.gold.core.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.clearFocusOnTapOutside(
    onClearFocus: () -> Unit,
): Modifier = pointerInput(onClearFocus) {
    detectTapGestures(onTap = { onClearFocus() })
}
