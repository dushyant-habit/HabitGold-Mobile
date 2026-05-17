package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class ExecuteSellOrderUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(orderId: String, vpaId: String) = repository.executeSellOrder(orderId, vpaId)
}

