package com.habit.gold.feature.home.data.repository

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.data.model.AppUpdateFeatureDto
import com.habit.gold.feature.home.data.model.PortfolioDashboardDto
import com.habit.gold.feature.home.data.model.PricePointDto
import com.habit.gold.feature.home.data.model.SipMandateBillingDto
import com.habit.gold.feature.home.data.model.SipMandateDto
import com.habit.gold.feature.home.data.model.TransactionDto
import com.habit.gold.feature.home.data.model.UserFeaturesResponseDto
import com.habit.gold.feature.home.data.remote.HomeRemoteDataSource
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.domain.model.HomeSipMandate
import com.habit.gold.feature.home.domain.model.HomeSipMandateBilling
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

class HomeRepositoryImpl(
    private val remoteDataSource: HomeRemoteDataSource,
) : HomeRepository {

    override suspend fun getPortfolioDashboard(): ApiResult<HomeDashboardSummary> {
        return when (val result = remoteDataSource.getPortfolioDashboard()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toDomain())
        }
    }

    override suspend fun getRecentTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<List<HomeRecentTransactionPreview>> {
        return when (val result = remoteDataSource.getRecentTransactions(page = page, limit = limit)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.data.map(TransactionDto::toDomain))
        }
    }

    override suspend fun getForceUpdate(): ApiResult<HomeForceUpdate?> {
        return when (val result = remoteDataSource.getUserFeatures()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.toForceUpdate())
        }
    }

    override suspend fun getSipMandates(): ApiResult<List<HomeSipMandate>> {
        return when (val result = remoteDataSource.getSipMandates()) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.map(SipMandateDto::toDomain))
        }
    }

    override suspend fun getPriceHistory(days: Int): ApiResult<List<HomeGoldPricePoint>> {
        return when (val result = remoteDataSource.getPriceHistory(days)) {
            is ApiResult.Failure -> result
            is ApiResult.Success -> ApiResult.Success(result.value.data.mapNotNull(PricePointDto::toDomain))
        }
    }
}

private fun PortfolioDashboardDto.toDomain(): HomeDashboardSummary {
    return HomeDashboardSummary(
        totalGoldBalanceGrams = totalGoldBalanceGrams.toDoubleSafely(),
        investedValue = investedValue.toDoubleSafely(),
        rewardsApplied = rewardsApplied?.toDoubleOrNull(),
        gstPaid = gstPaid.toDoubleSafely(),
        totalCost = totalCost.toDoubleSafely(),
        averageBuyPricePerGram = averageBuyPricePerGram.toDoubleSafely(),
        currentValue = currentValue.toDoubleSafely(),
        liveBuyPricePerGram = liveBuyPricePerGram.toDoubleSafely(),
        liveSellPricePerGram = liveSellPricePerGram.toDoubleSafely(),
        finalPayoutAmount = finalPayoutAmount.toDoubleSafely(),
        buySellPriceDifference = buySellPriceDifference.toDoubleSafely(),
    )
}

private fun TransactionDto.toDomain(): HomeRecentTransactionPreview {
    return HomeRecentTransactionPreview(
        id = id,
        type = type,
        status = status,
        amount = amount,
        goldQuantity = goldQuantity,
        createdAt = createdAt,
        isSip = isSip || sip != null,
        sipName = sip?.mandateName,
        sipFrequency = sip?.frequency,
    )
}

private fun UserFeaturesResponseDto.toForceUpdate(): HomeForceUpdate? {
    val candidate = forceUpdate?.takeIf { it.isEffectivelyActive() }
        ?: appUpdate?.takeIf { it.isEffectivelyActive() }
        ?: return null

    return HomeForceUpdate(
        title = candidate.title,
        message = candidate.message,
        ctaText = candidate.ctaText,
        updateUrl = candidate.updateUrl,
        storeUrl = candidate.storeUrl,
        minVersion = candidate.minVersion ?: candidate.minimumVersion,
        latestVersion = candidate.latestVersion,
        isForced = candidate.force == true || candidate.isForceUpdate == true,
    )
}

private fun AppUpdateFeatureDto.isEffectivelyActive(): Boolean {
    return isActive || force == true || isForceUpdate == true
}

private fun SipMandateDto.toDomain(): HomeSipMandate {
    return HomeSipMandate(
        id = id,
        name = name,
        amount = amount,
        frequency = frequency,
        startDate = startDate,
        status = status,
        promoCode = promoCode,
        nextExecutionDate = nextExecutionDate,
        billingCurrentAmount = billingCurrentAmount,
        billingNextExecutionAmount = billingNextExecutionAmount,
        billing = billing?.toDomain(),
    )
}

private fun SipMandateBillingDto.toDomain(): HomeSipMandateBilling {
    return HomeSipMandateBilling(
        nextExecutionAmount = nextExecutionAmount,
        currentAmount = currentAmount,
        needsAttention = needsAttention,
    )
}

private fun PricePointDto.toDomain(): HomeGoldPricePoint? {
    val parsedPrice = price.toDoubleOrNull() ?: return null
    val timestamp = runCatching {
        LocalDate.parse(date).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }.getOrNull() ?: return null
    return HomeGoldPricePoint(
        timestampMillis = timestamp,
        price = parsedPrice,
    )
}

private fun String?.toDoubleSafely(): Double = this?.toDoubleOrNull() ?: 0.0
