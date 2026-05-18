package com.habit.gold.feature.history.presentation

enum class HistoryTypeFilter {
    All,
    BuyGold,
    SellGold,
    Delivery,
}

enum class HistoryStatusFilter {
    Success,
    Refund,
    Failure,
    Pending,
}

enum class HistoryTransactionType {
    Buy,
    Sell,
    Delivery,
    Reward,
    Other,
}

enum class HistoryTransactionStatusKind {
    Success,
    Refund,
    Pending,
    Failure,
    Neutral,
}

data class HistoryTransactionItem(
    val id: String,
    val type: HistoryTransactionType,
    val dateLabel: String,
    val amountLabel: String,
    val weightLabel: String,
    val rawStatus: String,
    val isSip: Boolean,
    val sipName: String? = null,
    val sipFrequency: String? = null,
)

data class HistoryState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val hasMore: Boolean = false,
    val transactions: List<HistoryTransactionItem> = emptyList(),
    val visibleTransactions: List<HistoryTransactionItem> = emptyList(),
    val selectedTypeFilter: HistoryTypeFilter = HistoryTypeFilter.All,
    val selectedStatusFilter: HistoryStatusFilter? = null,
    val errorMessage: String? = null,
)

sealed interface HistoryIntent {
    data object Load : HistoryIntent
    data object Refresh : HistoryIntent
    data object LoadNextPage : HistoryIntent
    data class SelectTypeFilter(val filter: HistoryTypeFilter) : HistoryIntent
    data class SelectStatusFilter(val filter: HistoryStatusFilter) : HistoryIntent
    data object ClearStatusFilter : HistoryIntent
}
