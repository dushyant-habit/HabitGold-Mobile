package com.habit.gold.feature.rewards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.rewards.domain.model.RewardHistoryEntry
import com.habit.gold.feature.rewards.domain.usecase.GetRewardsHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class RewardsHistoryViewModel(
    private val getRewardsHistoryUseCase: GetRewardsHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsHistoryState())
    val state: StateFlow<RewardsHistoryState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val previousItems = _state.value.items.takeIf { it.isNotEmpty() }
        _state.value = if (previousItems != null) {
            _state.value.copy(
                isLoading = false,
                isRefreshing = true,
                errorMessage = null,
            )
        } else {
            RewardsHistoryState(isLoading = true)
        }

        viewModelScope.launch {
            when (val result = getRewardsHistoryUseCase()) {
                is ApiResult.Success -> {
                    _state.value = RewardsHistoryState(
                        isLoading = false,
                        items = result.value.map(::mapRewardHistoryRow),
                    )
                }

                is ApiResult.Failure -> {
                    _state.value = if (previousItems != null) {
                        RewardsHistoryState(
                            isLoading = false,
                            isRefreshing = false,
                            items = previousItems,
                        )
                    } else {
                        RewardsHistoryState(
                            isLoading = false,
                            errorMessage = result.error.message,
                        )
                    }
                }
            }
        }
    }
}

internal fun mapRewardHistoryRow(entry: RewardHistoryEntry): RewardHistoryRowUi {
    val normalizedKind = entry.kind.uppercase()
    val source = resolveRewardHistorySource(entry)
    val isCredit = when (normalizedKind) {
        "CREDIT" -> true
        "DEBIT" -> false
        else -> entry.direction.trim() == "+"
    }
    val sign = when {
        entry.direction.trim() == "-" -> "-"
        entry.direction.trim() == "+" -> "+"
        normalizedKind == "DEBIT" -> "-"
        else -> "+"
    }

    return RewardHistoryRowUi(
        id = entry.id,
        title = when (normalizedKind) {
            "DEBIT" -> debitTitle(source)
            else -> creditTitle(source)
        },
        dateLabel = formatRewardHistoryDate(entry.createdAt),
        expiryLabel = buildRewardHistoryExpiryLabel(entry, normalizedKind),
        amountLabel = buildRewardHistoryAmountLabel(sign = sign, amountInr = entry.amountInr),
        isCredit = isCredit,
        sourceChip = source.uppercase().replace('_', ' '),
        expired = entry.expired,
    )
}

private fun resolveRewardHistorySource(entry: RewardHistoryEntry): String {
    return entry.source?.trim()?.takeIf { it.isNotEmpty() }
        ?: entry.debitType?.trim()?.takeIf { it.isNotEmpty() }
        ?: if (entry.kind.equals("DEBIT", ignoreCase = true)) "DEBIT" else "UNKNOWN"
}

private fun buildRewardHistoryAmountLabel(
    sign: String,
    amountInr: String,
): String {
    val normalized = amountInr.trim().removePrefix("₹").replace(",", "")
    val parsed = normalized.toDoubleOrNull()
    val amountLabel = if (parsed != null) formatInr(parsed) else amountInr.trim().removePrefix("₹")
    return if (sign == "-") "-₹$amountLabel" else "+₹$amountLabel"
}

private fun debitTitle(source: String): String {
    return when (source.uppercase()) {
        "REDEMPTION", "REDEEM", "WITHDRAWAL" -> "Redeemed"
        "EXPIRY" -> "Expired balance removed"
        else -> sentenceCaseToken(source)
    }
}

private fun creditTitle(source: String): String {
    return when (source.uppercase()) {
        "CASHBACK" -> "Gold purchase reward"
        "MILESTONE" -> "Milestone reward"
        "REFERRAL" -> "Referral reward"
        else -> titleCaseToken(source).ifBlank { source }
    }
}

private fun buildRewardHistoryExpiryLabel(
    entry: RewardHistoryEntry,
    normalizedKind: String,
): String? {
    return when {
        entry.expired -> "Expired"
        entry.expiresAt != null && normalizedKind == "CREDIT" -> "Expiring ${formatRewardHistoryDate(entry.expiresAt)}"
        else -> null
    }
}

internal fun formatRewardHistoryDate(raw: String): String {
    return runCatching {
        val local = Instant.parse(raw).toLocalDateTime(TimeZone.UTC)
        "${local.day.toString().padStart(2, '0')} ${monthAbbreviation(local.month.name)}, ${local.year}"
    }.getOrElse { raw }
}

private fun monthAbbreviation(monthName: String): String {
    return monthName.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        .take(3)
}

private fun sentenceCaseToken(raw: String): String {
    val normalized = raw.replace('_', ' ').lowercase()
    return normalized.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun titleCaseToken(raw: String): String {
    return raw.replace('_', ' ')
        .lowercase()
        .split(' ')
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
