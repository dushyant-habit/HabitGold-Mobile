package com.habit.gold.feature.home.domain.usecase

import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.domain.HomeRepository
import com.habit.gold.feature.home.domain.model.HomeSummary

class LoadHomeSummaryUseCase(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): ApiResult<HomeSummary> {
        return when (val dashboardResult = repository.getPortfolioDashboard()) {
            is ApiResult.Failure -> dashboardResult
            is ApiResult.Success -> {
                val recentTransactions = when (
                    val transactionsResult = repository.getRecentTransactions(page = 1, limit = 5)
                ) {
                    is ApiResult.Success -> transactionsResult.value
                    is ApiResult.Failure -> emptyList()
                }
                val forceUpdate = when (val featuresResult = repository.getForceUpdate()) {
                    is ApiResult.Success -> featuresResult.value
                    is ApiResult.Failure -> null
                }
                val sipMandates = when (val mandatesResult = repository.getSipMandates()) {
                    is ApiResult.Success -> mandatesResult.value
                    is ApiResult.Failure -> emptyList()
                }

                ApiResult.Success(
                    HomeSummary(
                        dashboard = dashboardResult.value,
                        recentTransactions = recentTransactions,
                        forceUpdate = forceUpdate,
                        sipMandates = sipMandates,
                    )
                )
            }
        }
    }
}
