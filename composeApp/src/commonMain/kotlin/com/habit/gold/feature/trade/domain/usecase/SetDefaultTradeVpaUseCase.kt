package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class SetDefaultTradeVpaUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(vpaId: String) = repository.setDefaultVpa(vpaId)
}

