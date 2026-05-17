package com.habit.gold.feature.trade.domain.usecase

import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest

class ValidateTradeCouponUseCase(
    private val repository: TradeRepository,
) {
    suspend operator fun invoke(request: TradeCouponValidationRequest) = repository.validateCoupon(request)
}

