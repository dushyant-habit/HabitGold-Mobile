package com.habit.gold.feature.rewards.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberRewardsShareLauncher(): RewardsShareLauncher {
    val context = LocalContext.current
    return remember(context) { AndroidRewardsShareLauncher(context) }
}

private class AndroidRewardsShareLauncher(
    private val context: Context,
) : RewardsShareLauncher {
    override fun launch(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}
