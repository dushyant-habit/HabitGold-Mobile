package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class VerifyTradeVpaUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(vpa: String) = repository.verifyVpa(vpa)
}

