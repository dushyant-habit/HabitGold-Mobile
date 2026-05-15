package com.habit.gold.feature.home.data.remote

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.data.model.PriceHistoryDto
import com.habit.gold.core.network.safeApiCall
import com.habit.gold.feature.home.data.model.PortfolioDashboardDto
import com.habit.gold.feature.home.data.model.SipMandateDto
import com.habit.gold.feature.home.data.model.TransactionsResponseDto
import com.habit.gold.feature.home.data.model.UserFeaturesResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class HomeRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun getPortfolioDashboard(): ApiResult<PortfolioDashboardDto> = safeApiCall {
        httpClient.get("portfolio").body()
    }

    suspend fun getRecentTransactions(
        page: Int,
        limit: Int,
    ): ApiResult<TransactionsResponseDto> = safeApiCall {
        httpClient.get("trade/transactions") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }

    suspend fun getUserFeatures(): ApiResult<UserFeaturesResponseDto> = safeApiCall {
        httpClient.get("user/features").body()
    }

    suspend fun getSipMandates(): ApiResult<List<SipMandateDto>> = safeApiCall {
        httpClient.get("sip/mandates").body()
    }

    suspend fun getPriceHistory(days: Int): ApiResult<PriceHistoryDto> = safeApiCall {
        httpClient.get("gold/price/history") {
            parameter("days", days)
        }.body()
    }
}
