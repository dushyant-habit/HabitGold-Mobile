package com.habit.gold.feature.home.presentation

import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview

internal sealed interface HomeDestination {
    data object Dashboard : HomeDestination
    data class GoldValueDetails(val dashboard: HomeDashboardSummary?) : HomeDestination
    data object HelpCenter : HomeDestination
    data class TransactionDetails(val item: HomeRecentTransactionPreview) : HomeDestination
    data class Deferred(val target: HomeDeferredTarget) : HomeDestination
}

internal enum class HomeDeferredTarget {
    BuyGold,
    SellGold,
    Profile,
    Alerts,
    Savings,
}
