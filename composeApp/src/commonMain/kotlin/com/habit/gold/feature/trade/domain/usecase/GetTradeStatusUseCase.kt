package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class GetTradeStatusUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(orderId: String) = repository.getTradeStatus(orderId)
}

