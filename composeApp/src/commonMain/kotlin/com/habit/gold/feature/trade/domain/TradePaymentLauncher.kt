package com.habit.gold.feature.trade.domain

import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchRequest
import com.habit.gold.feature.trade.domain.model.TradePaymentLaunchResult

interface TradePaymentLauncher {
    suspend fun launch(request: TradePaymentLaunchRequest): TradePaymentLaunchResult
}

