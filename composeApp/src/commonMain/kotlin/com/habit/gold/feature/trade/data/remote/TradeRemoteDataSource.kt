package com.habit.gold.feature.trade.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.trade.data.model.TradeAvailableCouponDto
import com.habit.gold.feature.trade.data.model.TradeBuyOrderRequestDto
import com.habit.gold.feature.trade.data.model.TradeBuyOrderResponseDto
import com.habit.gold.feature.trade.data.model.TradeBuyPriceDto
import com.habit.gold.feature.trade.data.model.TradeCouponValidationRequestDto
import com.habit.gold.feature.trade.data.model.TradeCouponValidationResponseDto
import com.habit.gold.feature.trade.data.model.TradeDefaultVpaResponseDto
import com.habit.gold.feature.trade.data.model.TradeExecuteSellOrderRequestDto
import com.habit.gold.feature.trade.data.model.TradeInvoiceResponseDto
import com.habit.gold.feature.trade.data.model.TradeSellAvailabilityDto
import com.habit.gold.feature.trade.data.model.TradeSellOrderRequestDto
import com.habit.gold.feature.trade.data.model.TradeSellOrderResponseDto
import com.habit.gold.feature.trade.data.model.TradeSellPriceDto
import com.habit.gold.feature.trade.data.model.TradeStatusResponseDto
import com.habit.gold.feature.trade.data.model.TradeTransactionsResponseDto
import com.habit.gold.feature.trade.data.model.TradeUserVpaDto
import com.habit.gold.feature.trade.data.model.TradeVerifyVpaRequestDto
import com.habit.gold.feature.trade.data.model.TradeVerifyVpaResponseDto
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TradeRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getBuyPrice(): ApiResult<TradeBuyPriceDto> = safeApiCall {
        httpClient.get("gold/price/buy").body()
    }

    suspend fun getSellPrice(): ApiResult<TradeSellPriceDto> = safeApiCall {
        httpClient.get("gold/price/sell").body()
    }

    suspend fun createBuyOrder(
        idempotencyKey: String,
        body: TradeBuyOrderRequestDto,
    ): ApiResult<TradeBuyOrderResponseDto> = safeApiCall {
        httpClient.post("trade/buy") {
            header("Idempotency-Key", idempotencyKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun createSellOrder(
        idempotencyKey: String,
        body: TradeSellOrderRequestDto,
    ): ApiResult<TradeSellOrderResponseDto> = safeApiCall {
        httpClient.post("trade/sell") {
            header("Idempotency-Key", idempotencyKey)
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun executeSellOrder(
        body: TradeExecuteSellOrderRequestDto,
    ): ApiResult<TradeStatusResponseDto> = safeApiCall {
        httpClient.post("trade/sell/execute") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun getOrderStatus(orderId: String): ApiResult<TradeStatusResponseDto> = safeApiCall {
        httpClient.get("trade/orders/$orderId").body()
    }

    suspend fun getLegacyTransactionStatus(orderId: String): ApiResult<TradeStatusResponseDto> = safeApiCall {
        httpClient.get("trade/status/$orderId").body()
    }

    suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoiceResponseDto> = safeApiCall {
        httpClient.get("trade/orders/$orderId/invoice").body()
    }

    suspend fun getTradeTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<TradeTransactionsResponseDto> = safeApiCall {
        httpClient.get("trade/transactions") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getSellAvailability(): ApiResult<TradeSellAvailabilityDto> = safeApiCall {
        httpClient.get("portfolio/sell-availability").body()
    }

    suspend fun getUserVpas(): ApiResult<List<TradeUserVpaDto>> = safeApiCall {
        httpClient.get("user/vpa").body()
    }

    suspend fun setDefaultVpa(vpaId: String): ApiResult<TradeDefaultVpaResponseDto> = safeApiCall {
        httpClient.patch("user/vpa/$vpaId/set-default").body()
    }

    suspend fun verifyVpa(vpa: String): ApiResult<TradeVerifyVpaResponseDto> = safeApiCall {
        httpClient.post("user/vpa/verify") {
            contentType(ContentType.Application.Json)
            setBody(TradeVerifyVpaRequestDto(vpa = vpa))
        }.body()
    }

    suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double? = null,
        grams: Double? = null,
        deliveryFeeInr: Double? = null,
    ): ApiResult<List<TradeAvailableCouponDto>> = safeApiCall {
        httpClient.get("promo/coupons/available") {
            parameter("orderType", orderType.name)
            amount?.let { parameter("amount", it) }
            grams?.let { parameter("grams", it) }
            deliveryFeeInr?.let { parameter("deliveryFeeInr", it) }
        }.body()
    }

    suspend fun validateCoupon(
        body: TradeCouponValidationRequestDto,
    ): ApiResult<TradeCouponValidationResponseDto> = safeApiCall {
        httpClient.post("promo/coupons/validate") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }
}
