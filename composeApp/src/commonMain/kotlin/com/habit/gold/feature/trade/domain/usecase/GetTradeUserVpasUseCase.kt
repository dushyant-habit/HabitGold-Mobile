package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class GetTradeUserVpasUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke() = repository.getUserVpas()
}

