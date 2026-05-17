package com.habit.gold

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import platform.UIKit.UIDevice

@OptIn(ExperimentalNativeApi::class)
actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    name = UIDevice.currentDevice.systemName(),
    version = UIDevice.currentDevice.systemVersion,
    isDebugBinary = Platform.isDebugBinary,
)
