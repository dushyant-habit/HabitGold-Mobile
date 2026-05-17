package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class GetSellAvailabilityUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke() = repository.getSellAvailability()
}

