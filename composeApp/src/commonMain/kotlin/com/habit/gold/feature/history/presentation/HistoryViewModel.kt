package com.habit.gold.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.trade.domain.model.TradeTransactionPreview
import com.habit.gold.feature.trade.domain.usecase.GetTradeTransactionsUseCase
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.math.roundToInt
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HistoryViewModel(
    private val getTradeTransactionsUseCase: GetTradeTransactionsUseCase,
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private var currentPage = 1
    private var totalPages = 1
    private var isFetchingFirstPage = false
    private var isPaginating = false
    private var hasLoadedFirstPage = false
    private var lastTransactionsLoadedAtMillis: Long? = null
    private val fullTransactionList = mutableListOf<HistoryTransactionItem>()

    init {
        onIntent(HistoryIntent.Load)
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.Load -> fetchTransactions(force = false, isRefresh = false, pageToLoad = 1)
            HistoryIntent.Refresh -> fetchTransactions(force = true, isRefresh = true, pageToLoad = 1)
            HistoryIntent.LoadNextPage -> loadNextPage()
            is HistoryIntent.SelectTypeFilter -> {
                _state.value = deriveVisibleTransactions(
                    _state.value.copy(selectedTypeFilter = intent.filter),
                )
            }
            is HistoryIntent.SelectStatusFilter -> {
                _state.value = deriveVisibleTransactions(
                    _state.value.copy(selectedStatusFilter = intent.filter),
                )
            }
            HistoryIntent.ClearStatusFilter -> {
                _state.value = deriveVisibleTransactions(
                    _state.value.copy(selectedStatusFilter = null),
                )
            }
        }
    }

    private fun fetchTransactions(
        force: Boolean,
        isRefresh: Boolean,
        pageToLoad: Int,
    ) {
        if (pageToLoad == 1 && isFetchingFirstPage) return
        if (pageToLoad > 1 && (isFetchingFirstPage || isPaginating)) return

        val now = nowMillis()
        if (!force &&
            pageToLoad == 1 &&
            hasLoadedFirstPage &&
            lastTransactionsLoadedAtMillis?.let { now - it < TRANSACTIONS_CACHE_TTL_MS } == true
        ) {
            return
        }

        if (pageToLoad == 1) {
            isFetchingFirstPage = true
            val baseState = if (hasLoadedFirstPage) {
                _state.value.copy(
                    isLoading = false,
                    isRefreshing = true,
                    isPaginating = false,
                    errorMessage = null,
                )
            } else {
                _state.value.copy(
                    isLoading = true,
                    isRefreshing = false,
                    isPaginating = false,
                    errorMessage = null,
                )
            }
            _state.value = deriveVisibleTransactions(baseState)
        } else {
            isPaginating = true
            _state.value = deriveVisibleTransactions(
                _state.value.copy(
                    isPaginating = true,
                    errorMessage = null,
                )
            )
        }

        viewModelScope.launch {
            when (val result = getTradeTransactionsUseCase(page = pageToLoad, limit = 20)) {
                is ApiResult.Success -> {
                    totalPages = result.value.totalPages
                    val newItems = result.value.data.map(::mapTradeTransaction)
                    if (pageToLoad == 1) {
                        fullTransactionList.clear()
                        hasLoadedFirstPage = true
                        lastTransactionsLoadedAtMillis = nowMillis()
                    }
                    fullTransactionList.addAll(newItems)
                    currentPage = pageToLoad
                    isFetchingFirstPage = false
                    isPaginating = false
                    _state.value = deriveVisibleTransactions(
                        _state.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            hasMore = currentPage < totalPages,
                            transactions = fullTransactionList.toList(),
                            errorMessage = null,
                        )
                    )
                }

                is ApiResult.Failure -> {
                    isFetchingFirstPage = false
                    isPaginating = false
                    _state.value = if (hasLoadedFirstPage) {
                        deriveVisibleTransactions(
                            _state.value.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isPaginating = false,
                                hasMore = currentPage < totalPages,
                            )
                        )
                    } else {
                        deriveVisibleTransactions(
                            _state.value.copy(
                                isLoading = false,
                                isRefreshing = false,
                                isPaginating = false,
                                hasMore = false,
                                errorMessage = result.error.message,
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadNextPage() {
        if (isFetchingFirstPage || isPaginating || currentPage >= totalPages) return
        fetchTransactions(force = true, isRefresh = false, pageToLoad = currentPage + 1)
    }
}

private fun deriveVisibleTransactions(state: HistoryState): HistoryState {
    val visibleTransactions = state.transactions.filter { transaction ->
        val matchesType = when (state.selectedTypeFilter) {
            HistoryTypeFilter.All -> true
            HistoryTypeFilter.BuyGold -> transaction.type == HistoryTransactionType.Buy
            HistoryTypeFilter.SellGold -> transaction.type == HistoryTransactionType.Sell
            HistoryTypeFilter.Delivery -> transaction.type == HistoryTransactionType.Delivery
        }

        val statusKind = transaction.rawStatus.toHistoryStatusKind()
        val matchesStatus = when (state.selectedStatusFilter) {
            null -> {
                statusKind == HistoryTransactionStatusKind.Success ||
                    statusKind == HistoryTransactionStatusKind.Refund ||
                    transaction.shouldShowInDefaultHistory()
            }
            HistoryStatusFilter.Success -> statusKind == HistoryTransactionStatusKind.Success
            HistoryStatusFilter.Refund -> statusKind == HistoryTransactionStatusKind.Refund
            HistoryStatusFilter.Failure -> statusKind == HistoryTransactionStatusKind.Failure
            HistoryStatusFilter.Pending -> statusKind == HistoryTransactionStatusKind.Pending
        }

        matchesType && matchesStatus
    }

    return state.copy(visibleTransactions = visibleTransactions)
}

internal fun mapTradeTransaction(transaction: TradeTransactionPreview): HistoryTransactionItem {
    val normalizedType = transaction.type.trim().uppercase()
    val type = when (normalizedType) {
        "BUY" -> HistoryTransactionType.Buy
        "SELL" -> HistoryTransactionType.Sell
        "DELIVERY", "DELIVERY_WITHDRAWAL" -> HistoryTransactionType.Delivery
        "REWARD" -> HistoryTransactionType.Reward
        else -> HistoryTransactionType.Other
    }
    val isSipTransaction = transaction.isSip || !transaction.sipFrequency.isNullOrBlank() || !transaction.sipName.isNullOrBlank()
    val sipName = transaction.sipName?.takeIf { it.isNotBlank() }
    val weightPrefix = when (type) {
        HistoryTransactionType.Buy -> "+"
        HistoryTransactionType.Sell, HistoryTransactionType.Delivery -> "-"
        else -> ""
    }

    return HistoryTransactionItem(
        id = transaction.id,
        type = type,
        dateLabel = formatHistoryDateTime(transaction.createdAt),
        amountLabel = "₹${normalizeMoneyDisplay(transaction.amount)}",
        weightLabel = "$weightPrefix${normalizeGoldDisplay(transaction.goldQuantity)} g",
        rawStatus = transaction.status,
        isSip = isSipTransaction,
        sipName = sipName,
        sipFrequency = transaction.sipFrequency?.takeIf { it.isNotBlank() },
    )
}

internal fun String.toHistoryStatusKind(): HistoryTransactionStatusKind {
    val normalized = lowercase()
    return when {
        normalized.contains("refund") -> HistoryTransactionStatusKind.Refund
        normalized.contains("fail") ||
            normalized.contains("cancel") ||
            normalized.contains("reject") ||
            normalized.contains("expired") -> HistoryTransactionStatusKind.Failure
        normalized.contains("pending") ||
            normalized.contains("process") ||
            normalized.contains("progress") ||
            normalized.contains("initiated") ||
            normalized.contains("await") -> HistoryTransactionStatusKind.Pending
        normalized.contains("complete") ||
            normalized.contains("success") ||
            normalized.contains("credit") ||
            normalized.contains("confirm") ||
            normalized.contains("deliver") ||
            normalized.contains("paid") -> HistoryTransactionStatusKind.Success
        else -> HistoryTransactionStatusKind.Neutral
    }
}

internal fun String.toHistoryStatusLabel(): String {
    val normalized = trim()
        .replace('_', ' ')
        .replace('-', ' ')
        .lowercase()
    if (normalized.isBlank()) return "--"
    if (normalized == "pending") return "In Progress"
    return normalized
        .split(Regex("\\s+"))
        .joinToString(" ") { token ->
            token.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
}

internal fun HistoryTransactionItem.shouldShowInDefaultHistory(): Boolean {
    return type == HistoryTransactionType.Sell && rawStatus.equals("PAYOUT_PROCESSING", ignoreCase = true)
}

internal fun formatHistoryDateTime(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.UTC)
        val hour24 = local.hour
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val meridiem = if (hour24 >= 12) "PM" else "AM"
        "${local.day.toString().padStart(2, '0')} ${historyMonthAbbreviation(local.month.name)} ${local.year}, " +
            "${hour12.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')} $meridiem"
    }.getOrElse { raw }
}

private fun historyMonthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}

private fun normalizeMoneyDisplay(raw: String): String {
    val parsed = raw.trim().removePrefix("₹").replace(",", "").toDoubleOrNull() ?: return raw
    val rounded = ((parsed * 100).roundToInt() / 100.0)
    val absolute = rounded.absoluteValue
    val whole = absolute.toLong()
    val decimals = ((absolute - whole) * 100).roundToInt().coerceAtLeast(0)
    val groupedWhole = formatIndianWhole(whole)
    val decimalSuffix = ".${decimals.toString().padStart(2, '0')}"
    val sign = if (rounded < 0) "-" else ""
    return "$sign$groupedWhole$decimalSuffix"
}

private fun normalizeGoldDisplay(raw: String): String {
    val parsed = raw.trim().removeSuffix("g").trim().toDoubleOrNull() ?: return raw.trim()
    val rounded = ((parsed * 10_000).roundToInt() / 10_000.0)
    val text = rounded.toString()
    return if (!text.contains('.')) {
        text
    } else {
        text.trimEnd('0').trimEnd('.')
    }
}

private fun formatIndianWhole(value: Long): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits
    val lastThree = digits.takeLast(3)
    var prefix = digits.dropLast(3)
    val groups = mutableListOf<String>()
    while (prefix.length > 2) {
        groups += prefix.takeLast(2)
        prefix = prefix.dropLast(2)
    }
    if (prefix.isNotEmpty()) groups += prefix
    return groups.asReversed().joinToString(",") + ",$lastThree"
}

private const val TRANSACTIONS_CACHE_TTL_MS = 60_000L
