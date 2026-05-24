package com.habit.gold.feature.trade.data.repository

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.data.model.TradeAvailableCouponDto
import com.habit.gold.feature.trade.data.model.TradeBuyOrderRequestDto
import com.habit.gold.feature.trade.data.model.TradeBuyOrderResponseDto
import com.habit.gold.feature.trade.data.model.TradeBuyPriceDto
import com.habit.gold.feature.trade.data.model.TradeCouponValidationRequestDto
import com.habit.gold.feature.trade.data.model.TradeCouponValidationResponseDto
import com.habit.gold.feature.trade.data.model.TradeSellAvailabilityDto
import com.habit.gold.feature.trade.data.model.TradeSellOrderRequestDto
import com.habit.gold.feature.trade.data.model.TradeSellOrderResponseDto
import com.habit.gold.feature.trade.data.model.TradeSellPriceDto
import com.habit.gold.feature.trade.data.model.TradeStatusResponseDto
import com.habit.gold.feature.trade.data.model.TradeTransactionDto
import com.habit.gold.feature.trade.data.model.TradeUserVpaDto
import com.habit.gold.feature.trade.data.remote.TradeRemoteDataSource
import com.habit.gold.feature.trade.domain.TradeRepository
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon
import com.habit.gold.feature.trade.domain.model.TradeBuyOrder
import com.habit.gold.feature.trade.domain.model.TradeBuyPrice
import com.habit.gold.feature.trade.domain.model.TradeLivePrice
import com.habit.gold.feature.trade.domain.model.TradeBuyOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeCouponOrderType
import com.habit.gold.feature.trade.domain.model.TradeCouponValidation
import com.habit.gold.feature.trade.domain.model.TradeCouponValidationRequest
import com.habit.gold.feature.trade.domain.model.TradeInvoice
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeSellPrice
import com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest
import com.habit.gold.feature.trade.domain.model.TradeStatus
import com.habit.gold.feature.trade.domain.model.TradeTransactionPreview
import com.habit.gold.feature.trade.domain.model.TradeTransactionsPage
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.model.TradeVpaVerification
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

class TradeRepositoryImpl(
    private val remoteDataSource: TradeRemoteDataSource,
) : TradeRepository {
    override suspend fun getLivePrice(): ApiResult<TradeLivePrice> {
        val buyResult = remoteDataSource.getBuyPrice()
        if (buyResult is ApiResult.Failure) return buyResult

        val sellResult = remoteDataSource.getSellPrice()
        if (sellResult is ApiResult.Failure) return sellResult

        val buyPrice = (buyResult as ApiResult.Success).value.toDomain()
        val sellPrice = (sellResult as ApiResult.Success).value.toDomain()
        return ApiResult.Success(
            TradeLivePrice(
                buy = buyPrice.price,
                sell = sellPrice.price,
                buyRateId = buyPrice.rateId,
                sellRateId = sellPrice.rateId,
                taxPc = buyPrice.taxPc,
                sourceTimestamp = buyPrice.sourceTimestamp,
                buyValidUntil = buyPrice.validUntil,
                sellValidUntil = sellPrice.validUntil,
            )
        )
    }

    override suspend fun createBuyOrder(request: TradeBuyOrderRequest): ApiResult<TradeBuyOrder> {
        val dto = TradeBuyOrderRequestDto(
            amount = request.amount?.let(::normalizeAmount),
            grams = request.grams?.let(::normalizeGrams),
            buyRateId = request.buyRateId,
            couponCode = request.couponCode?.trim()?.takeIf { it.isNotEmpty() },
            useRewardsInr = request.useRewardsInr?.let(::normalizeAmount)?.takeIf { it > 0.0 },
        )
        return when (val result = remoteDataSource.createBuyOrder(generateIdempotencyKey("buy"), dto)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun createSellOrder(request: TradeSellOrderRequest): ApiResult<TradeSellOrder> {
        val dto = TradeSellOrderRequestDto(
            grams = normalizeGrams(request.grams),
            sellRateId = request.sellRateId,
        )
        return when (val result = remoteDataSource.createSellOrder(generateIdempotencyKey("sell"), dto)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun executeSellOrder(orderId: String, vpaId: String): ApiResult<TradeStatus> {
        return when (val result = remoteDataSource.executeSellOrder(
            body = com.habit.gold.feature.trade.data.model.TradeExecuteSellOrderRequestDto(
                orderId = orderId,
                vpaId = vpaId,
            )
        )) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getTradeStatus(orderId: String): ApiResult<TradeStatus> {
        val primary = remoteDataSource.getOrderStatus(orderId)
        if (primary is ApiResult.Success) {
            return ApiResult.Success(primary.value.toDomain())
        }

        val primaryFailure = primary as ApiResult.Failure
        val shouldFallback = primaryFailure.error.statusCode == 404 || primaryFailure.error.statusCode == 405
        if (!shouldFallback) return primaryFailure

        return when (val fallback = remoteDataSource.getLegacyTransactionStatus(orderId)) {
            is ApiResult.Failure -> fallback
            is ApiResult.Success -> ApiResult.Success(fallback.value.toDomain())
        }
    }

    override suspend fun getTradeInvoice(orderId: String): ApiResult<TradeInvoice> {
        return when (val result = remoteDataSource.getTradeInvoice(orderId)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(TradeInvoice(invoiceUrl = result.value.invoiceUrl.trim()))
        }
    }

    override suspend fun getTradeTransactions(page: Int, limit: Int): ApiResult<TradeTransactionsPage> {
        return when (val result = remoteDataSource.getTradeTransactions(page = page, limit = limit)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(
                TradeTransactionsPage(
                    data = result.value.data.map(TradeTransactionDto::toDomain),
                    total = result.value.meta.total,
                    page = result.value.meta.page,
                    limit = result.value.meta.limit,
                    totalPages = result.value.meta.totalPages,
                )
            )
        }
    }

    override suspend fun getSellAvailability(): ApiResult<TradeSellAvailability> {
        return when (val result = remoteDataSource.getSellAvailability()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getUserVpas(): ApiResult<List<TradeUserVpa>> {
        return when (val result = remoteDataSource.getUserVpas()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.map(TradeUserVpaDto::toDomain))
        }
    }

    override suspend fun setDefaultVpa(vpaId: String): ApiResult<Unit> {
        return when (val result = remoteDataSource.setDefaultVpa(vpaId)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(Unit)
        }
    }

    override suspend fun verifyVpa(vpa: String): ApiResult<TradeVpaVerification> {
        return when (val result = remoteDataSource.verifyVpa(vpa.trim())) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(
                TradeVpaVerification(
                    message = result.value.message,
                    vpaId = result.value.vpaId,
                    isVerified = result.value.isVerified,
                )
            )
        }
    }

    override suspend fun getAvailableCoupons(
        orderType: TradeCouponOrderType,
        amount: Double?,
        grams: Double?,
        deliveryFeeInr: Double?,
    ): ApiResult<List<TradeAvailableCoupon>> {
        return when (val result = remoteDataSource.getAvailableCoupons(orderType, amount, grams, deliveryFeeInr)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.map(TradeAvailableCouponDto::toDomain))
        }
    }

    override suspend fun validateCoupon(request: TradeCouponValidationRequest): ApiResult<TradeCouponValidation> {
        val dto = TradeCouponValidationRequestDto(
            orderType = request.orderType,
            code = request.code.trim(),
            amount = request.amount?.let(::normalizeAmount),
            grams = request.grams?.let(::normalizeGrams),
            deliveryFeeInr = request.deliveryFeeInr?.let(::normalizeAmount),
            deliveryGrams = request.deliveryGrams?.let(::normalizeGrams),
        )
        return when (val result = remoteDataSource.validateCoupon(dto)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }
}

private fun TradeBuyPriceDto.toDomain(): TradeBuyPrice {
    return TradeBuyPrice(
        price = price,
        rateId = rateId,
        validUntil = validUntil,
        taxPc = taxPc,
        sourceTimestamp = sourceTimestamp,
    )
}

private fun TradeSellPriceDto.toDomain(): TradeSellPrice {
    return TradeSellPrice(
        price = price,
        rateId = rateId,
        validUntil = validUntil,
        sourceTimestamp = sourceTimestamp,
    )
}

private fun TradeBuyOrderResponseDto.toDomain(): TradeBuyOrder {
    return TradeBuyOrder(
        orderId = orderId,
        status = status,
        paymentProvider = paymentProvider,
        paymentProviderOrderId = paymentProviderOrderId,
        priceLockId = priceLockId,
        priceLockExpiresAt = priceLockExpiresAt,
        goldQuantityGrams = goldQuantity.toDoubleOrNull() ?: 0.0,
        goldPricePerGram = goldPrice.toDoubleOrNull() ?: 0.0,
        gstGrossAmount = gst.grossAmount.toDoubleOrNull() ?: 0.0,
        gstNetAmount = gst.netAmount.toDoubleOrNull() ?: 0.0,
        gstAmount = gst.gstAmount.toDoubleOrNull() ?: 0.0,
        sdkPayloadJson = sdkPayload?.toString(),
    )
}

private fun TradeSellOrderResponseDto.toDomain(): TradeSellOrder {
    val gramsValue = grams.toDoubleOrNull() ?: 0.0
    val goldPriceValue = goldPrice.toDoubleOrNull() ?: 0.0
    val amountValue = amount.toDoubleOrNull()
        ?: roundToScale(gramsValue * goldPriceValue, decimals = 2)
    return TradeSellOrder(
        orderId = orderId,
        status = status,
        priceLockId = priceLockId,
        priceLockExpiresAt = priceLockExpiresAt,
        goldQuantityGrams = gramsValue,
        goldPricePerGram = goldPriceValue,
        payoutAmount = amountValue,
        transactionId = transactionId,
    )
}

private fun TradeStatusResponseDto.toDomain(): TradeStatus {
    return TradeStatus(
        orderId = orderId,
        status = status,
        message = message,
    )
}

private fun TradeTransactionDto.toDomain(): TradeTransactionPreview {
    return TradeTransactionPreview(
        id = id,
        type = type,
        status = status,
        amount = amount,
        gstAmount = gstAmount,
        netAmount = netAmount,
        goldQuantity = goldQuantity,
        goldPrice = goldPrice,
        createdAt = createdAt,
        isSip = isSip || sip != null,
        sipMandateId = sip?.mandateId,
        sipExecutionId = sip?.executionId,
        sipName = sip?.mandateName,
        sipFrequency = sip?.frequency,
    )
}

private fun TradeSellAvailabilityDto.toDomain(): TradeSellAvailability {
    return TradeSellAvailability(
        totalGoldBalanceGrams = totalGoldBalanceGrams.toDoubleOrNull() ?: 0.0,
        sellableGoldBalanceGrams = sellableGoldBalanceGrams.toDoubleOrNull() ?: 0.0,
        lockedGoldBalanceGrams = lockedGoldBalanceGrams.toDoubleOrNull() ?: 0.0,
        nextSellableAt = nextSellableAt,
    )
}

private fun TradeUserVpaDto.toDomain(): TradeUserVpa {
    return TradeUserVpa(
        id = id,
        address = address,
        holderName = holderName,
        isVerified = isVerified,
        isDefault = isDefault,
    )
}

private fun TradeAvailableCouponDto.toDomain(): TradeAvailableCoupon {
    return TradeAvailableCoupon(
        code = code,
        description = description,
        type = type,
        estimatedSaving = estimatedSaving,
        maxDiscountAmount = maxDiscountAmount,
        minOrderValue = minOrderValue,
        expiresAt = expiresAt,
        applicableOrderTypes = applicableOrderTypes,
        isAssigned = isAssigned,
    )
}

private fun TradeCouponValidationResponseDto.toDomain(): TradeCouponValidation {
    return TradeCouponValidation(
        code = code,
        promoRuleId = promoRuleId,
        promotionalDiscount = promotionalDiscount,
        promotionalCashback = promotionalCashback,
        promotionalExtraGold = promotionalExtraGold,
        promotionalDeliveryDiscount = promotionalDeliveryDiscount,
        netOrderAmount = netOrderAmount,
        netDeliveryFeeInr = netDeliveryFeeInr,
    )
}

private fun normalizeAmount(value: Double): Double {
    return roundToScale(value, decimals = 2)
}

private fun normalizeGrams(value: Double): Double {
    return roundToScale(value, decimals = 4)
}

private fun roundToScale(value: Double, decimals: Int): Double {
    val factor = 10.0.pow(decimals.toDouble())
    return round(value * factor) / factor
}

private fun generateIdempotencyKey(prefix: String): String {
    val firstSuffix = Random.nextLong(100_000_000_000L, 999_999_999_999L)
    val secondSuffix = Random.nextLong(100_000_000_000L, 999_999_999_999L)
    return "$prefix-$firstSuffix-$secondSuffix"
}
