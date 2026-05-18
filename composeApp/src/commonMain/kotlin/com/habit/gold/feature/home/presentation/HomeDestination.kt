package com.habit.gold.feature.home.presentation

import com.habit.gold.feature.home.domain.model.HomeDashboardSummary
import com.habit.gold.feature.home.domain.model.HomeRecentTransactionPreview
import com.habit.gold.feature.profile.presentation.ProfileDestination
import com.habit.gold.feature.savings.presentation.SavingsDestination
import com.habit.gold.feature.delivery.presentation.DeliveryDestination
import com.habit.gold.feature.trade.presentation.TradeDestination

internal sealed interface HomeDestination {
    data object Dashboard : HomeDestination
    data object Alerts : HomeDestination
    data class GoldValueDetails(val dashboard: HomeDashboardSummary?) : HomeDestination
    data class Profile(
        val destination: ProfileDestination,
        val returnDestination: HomeDestination = Dashboard,
    ) : HomeDestination
    data class Savings(
        val destination: SavingsDestination,
        val returnDestination: HomeDestination = Dashboard,
    ) : HomeDestination
    data class TransactionDetails(val item: HomeRecentTransactionPreview) : HomeDestination
    data class Trade(val destination: TradeDestination) : HomeDestination
    data class Delivery(
        val destination: DeliveryDestination = DeliveryDestination.Catalog,
        val returnDestination: HomeDestination = Dashboard,
    ) : HomeDestination
    data class Deferred(val target: HomeDeferredTarget) : HomeDestination
}

internal enum class HomeDeferredTarget {
    Profile,
    Alerts,
}
