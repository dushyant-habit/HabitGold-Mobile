package com.habit.gold.feature.savings.presentation

sealed interface SavingsDestination {
    data object Manage : SavingsDestination

    data class Setup(
        val frequency: String,
        val initialAmount: String? = null,
        val mandateId: String? = null,
        val initialExecutionDay: Int? = null,
        val initialStatus: String? = null,
    ) : SavingsDestination
}
