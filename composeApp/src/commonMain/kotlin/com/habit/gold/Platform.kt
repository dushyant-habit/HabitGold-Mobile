package com.habit.gold

import kotlinx.serialization.Serializable

@Serializable
data class PlatformInfo(
    val name: String,
    val version: String,
    val isDebugBinary: Boolean,
)

expect fun getPlatformInfo(): PlatformInfo
