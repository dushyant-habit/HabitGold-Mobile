package com.habit.gold

import platform.UIKit.UIDevice

actual fun getPlatformInfo(): PlatformInfo = PlatformInfo(
    name = UIDevice.currentDevice.systemName(),
    version = UIDevice.currentDevice.systemVersion,
    isDebugBinary = true,
)
