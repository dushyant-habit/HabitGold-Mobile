package com.habit.gold.core.platform.analytics

import com.microsoft.clarity.Clarity

actual fun setPlatformScreenName(screenName: String) {
    runCatching {
        Clarity.setCurrentScreenName(screenName)
    }
}
