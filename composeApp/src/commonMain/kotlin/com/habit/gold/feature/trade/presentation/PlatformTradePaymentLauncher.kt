package com.habit.gold.feature.trade.presentation

import androidx.compose.runtime.Composable
import com.habit.gold.feature.trade.domain.TradePaymentLauncher

@Composable
expect fun rememberPlatformTradePaymentLauncher(): TradePaymentLauncher
