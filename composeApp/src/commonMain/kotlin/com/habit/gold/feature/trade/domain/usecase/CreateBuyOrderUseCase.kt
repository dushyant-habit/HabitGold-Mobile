package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest

class CreateBuyOrderUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(request: TradeBuyOrderRequest) = repository.createBuyOrder(request)
}

