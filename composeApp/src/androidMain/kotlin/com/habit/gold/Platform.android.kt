package com.habit.gold

import android.os.Build

actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    name = "Android",
    version = "API ${Build.VERSION.SDK_INT}",
    isDebugBinary = BuildConfig.DEBUG,
)
