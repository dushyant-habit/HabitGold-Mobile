package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType

class GetTradeAvailableCouponsUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(
        orderType: TradeCouponOrderType,
        amount: Double? = null,
        grams: Double? = null,
        deliveryFeeInr: Double? = null,
    ) = repository.getAvailableCoupons(orderType, amount, grams, deliveryFeeInr)
}

