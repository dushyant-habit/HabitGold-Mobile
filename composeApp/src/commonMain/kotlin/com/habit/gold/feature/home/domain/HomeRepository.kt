package com.habit.gold.feature.home.domain

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeForceUpdate
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.home.domain.model.HomeSipMandate

interface HomeRepository {
    suspend fun getPortfolioDashboard(): ApiResult<HomeDashboardSummary>
    suspend fun getRecentTransactions(page: Int, limit: Int): ApiResult<List<HomeRecentTransactionPreview>>
    suspend fun getForceUpdate(): ApiResult<HomeForceUpdate?>
    suspend fun getSipMandates(): ApiResult<List<HomeSipMandate>>
    suspend fun getPriceHistory(days: Int): ApiResult<List<HomeGoldPricePoint>>
}
