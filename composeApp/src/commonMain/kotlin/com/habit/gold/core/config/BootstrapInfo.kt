package com.habit.gold.core.config

import com.habit.gold.PlatformInfo

data class BootstrapCheck(
    val title: String,
    val detail: String,
)

data class BootstrapInfo(
    val appConfig: AppConfig,
    val platformInfo: PlatformInfo,
    val checks: List<BootstrapCheck>,
    val nextSteps: List<String>,
)
