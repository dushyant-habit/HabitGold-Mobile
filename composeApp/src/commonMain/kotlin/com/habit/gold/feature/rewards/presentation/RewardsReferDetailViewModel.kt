package com.habit.gold.feature.rewards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.presentation.formatInr
import com.habit.gold.feature.rewards.domain.model.ReferDetails
import com.habit.gold.feature.rewards.domain.usecase.GetReferDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardsReferDetailViewModel(
    private val getReferDetailsUseCase: GetReferDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(RewardsReferDetailState())
    val state: StateFlow<RewardsReferDetailState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val previousUi = _state.value.ui
        _state.value = _state.value.copy(
            isLoading = true,
            ui = previousUi,
            errorMessage = null,
        )
        viewModelScope.launch {
            when (val result = getReferDetailsUseCase()) {
                is ApiResult.Success -> {
                    _state.value = RewardsReferDetailState(
                        isLoading = false,
                        ui = mapReferDetailUi(result.value),
                    )
                }

                is ApiResult.Failure -> {
                    _state.value = RewardsReferDetailState(
                        isLoading = false,
                        ui = previousUi,
                        errorMessage = result.error.message,
                    )
                }
            }
        }
    }
}

internal fun mapReferDetailUi(dto: ReferDetails): RewardsReferDetailUi {
    val fraction = dto.currentPercentage
        ?.trim()
        ?.removeSuffix("%")
        ?.toFloatOrNull()
        ?.div(100f)
        ?.takeIf { it > 0f }
        ?: 0.005f

    return RewardsReferDetailUi(
        lifetimeEarningsDisplay = formatLifetimeEarnings(dto.lifetimeEarnings),
        activeFriendsCount = dto.activeReferrals,
        boosterIsActive = dto.boosterIsActive,
        cashbackPercentLabel = formatPercentLabel(dto.currentPercentage),
        daysLeft = dto.daysLeft,
        totalDaysCap = dto.totalDaysCap,
        estimateCashbackFraction = fraction,
        referralCode = resolveReferralCode(dto),
        buyExtensionTitle = extendTitle(dto.buyBonusDays, fallbackDays = 7),
        buyExtensionSubtitle = "With every ${formatInrAmount(dto.buyThresholdInr)} purchase",
        referralExtensionTitle = extendTitle(dto.referralFirstBuyDays, fallbackDays = 14),
        referralExtensionSubtitle = "Refer a friend who buys gold",
        sipExtensionTitle = extendTitle(dto.sipBonusDays, fallbackDays = 7),
        sipExtensionSubtitle = sipSubtitle(dto),
    )
}

internal fun referralInviteLink(referralCode: String): String {
    val normalizedCode = referralCode.trim().uppercase()
    return "https://play.google.com/store/apps/details?id=com.habit.gold&referrer=referralCode%3D$normalizedCode"
}

internal fun referralInviteMessage(referralCode: String): String {
    return """
        Start saving in Real Gold with just ₹100. Get 0.5% Gold Rewards on Every Purchase.
        
        Invest in a Habit. Invest in Gold.

        Join using my referral link 👇
        ${referralInviteLink(referralCode)}
    """.trimIndent()
}

private fun resolveReferralCode(dto: ReferDetails): String {
    val code = dto.referralList.firstOrNull { it.isDefault }?.code?.takeIf { it.isNotBlank() }
        ?: dto.referralList.firstOrNull { it.code.isNotBlank() }?.code
        ?: dto.referralCode?.trim()?.takeIf { it.isNotBlank() }
    return code ?: "SAVEGOLD20"
}

private fun extendTitle(days: Int, fallbackDays: Int): String {
    val normalized = if (days > 0) days else fallbackDays
    return "Extend for +$normalized days"
}

private fun sipSubtitle(dto: ReferDetails): String {
    val amount = formatInrAmount(dto.sipMinAmountInr)
    val bonus = dto.sipBonusDays.takeIf { it > 0 } ?: 1
    return "Weekly $amount SIP = +$bonus days"
}

private fun formatLifetimeEarnings(raw: String?): String {
    val trimmed = raw?.trim().orEmpty()
    return when {
        trimmed.isBlank() -> "₹0"
        trimmed.startsWith("₹") -> trimmed
        else -> "₹$trimmed"
    }
}

private fun formatInrAmount(raw: String?): String {
    val normalized = raw?.trim()?.removePrefix("₹")?.replace(",", "").orEmpty()
    val parsed = normalized.toDoubleOrNull()
    return if (parsed == null) {
        if (normalized.isBlank()) "₹0" else "₹$normalized"
    } else {
        "₹${formatInr(parsed)}"
    }
}

private fun formatPercentLabel(raw: String?): String {
    val trimmed = raw?.trim().orEmpty()
    return when {
        trimmed.isBlank() -> "0.5%"
        trimmed.endsWith("%") -> trimmed
        else -> "$trimmed%"
    }
}
