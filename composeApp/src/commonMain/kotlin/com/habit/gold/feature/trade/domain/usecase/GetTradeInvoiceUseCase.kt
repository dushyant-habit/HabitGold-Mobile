package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository

class GetTradeInvoiceUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(orderId: String) = repository.getTradeInvoice(orderId)
}

