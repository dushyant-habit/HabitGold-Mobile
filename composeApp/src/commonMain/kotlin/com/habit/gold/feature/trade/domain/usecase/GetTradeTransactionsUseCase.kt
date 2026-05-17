package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class GetTradeTransactionsUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(page: Int, limit: Int) = repository.getTradeTransactions(page, limit)
}

