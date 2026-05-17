package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest

class CreateSellOrderUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(request: TradeSellOrderRequest) = repository.createSellOrder(request)
}

