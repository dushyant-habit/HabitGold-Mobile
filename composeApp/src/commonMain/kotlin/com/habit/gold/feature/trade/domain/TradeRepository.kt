package com.habit.gold.feature.trade.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradeLivePrice
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradeInvoice
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeStatus
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification

interface TradeRepository {
    suspend fun getLivePrice(): ApiResult<TradeLivePrice>
    suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder>
    suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder>
    suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus>
    suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus>
    suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice>
    suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage>
    suspend fun getSellAvailability(): ApiResult<TradeSellAvailability>
    suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>>
    suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit>
    suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification>
    suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double? = null,
        grams: Double? = null,
        deliveryFeeInr: Double? = null,
    ): ApiResult<List<TradeAvailableCoupon>>
    suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation>
}
