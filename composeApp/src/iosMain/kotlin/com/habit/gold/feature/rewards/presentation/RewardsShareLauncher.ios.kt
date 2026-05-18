package com.habit.gold.feature.rewards.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

@Composable
internal actual fun rememberRewardsShareLauncher(): RewardsShareLauncher {
    return remember { IosRewardsShareLauncher() }
}

private class IosRewardsShareLauncher : RewardsShareLauncher {
    @OptIn(ExperimentalForeignApi::class)
    override fun launch(text: String) {
        val presenter = topViewController() ?: return
        val activityController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null,
        )
        presenter.presentViewController(activityController, animated = true, completion = null)
    }
}

private fun topViewController(): UIViewController? {
    val root = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return null
    var top = root
    while (top.presentedViewController != null) {
        top = top.presentedViewController!!
    }
    return top
}
