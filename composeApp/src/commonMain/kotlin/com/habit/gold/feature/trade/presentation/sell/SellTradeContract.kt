package com.habit.gold.feature.trade.presentation.sell

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.trade.domain.model.TradePollingSnapshot
import com.habit.gold.feature.trade.domain.model.TradeSellAvailability
import com.habit.gold.feature.trade.domain.model.TradeSellOrder
import com.habit.gold.feature.trade.domain.model.TradeUserVpa

data class SellTradeDraftRequest(
    val grams: Double,
    val sellRateId: String,
    val estimatedPayoutAmount: Double,
)

enum class SellTradeEntryMode {
    Rupees,
    Grams,
}

sealed interface SellTradeStep {
    data object Entry : SellTradeStep
    data object PayoutVpa : SellTradeStep
    data object Success : SellTradeStep
    data object Failure : SellTradeStep
    data object Pending : SellTradeStep
}

data class SellTradeState(
    val isLoading: Boolean = false,
    val step: SellTradeStep = SellTradeStep.Entry,
    val entryMode: SellTradeEntryMode = SellTradeEntryMode.Rupees,
    val availability: TradeSellAvailability? = null,
    val draftRequest: SellTradeDraftRequest? = null,
    val createdOrder: TradeSellOrder? = null,
    val selectedVpaId: String? = null,
    val userVpas: List<TradeUserVpa> = emptyList(),
    val pollingSnapshot: TradePollingSnapshot? = null,
    val errorMessage: String? = null,
) : MviState

sealed interface SellTradeIntent : MviIntent {
    data object Load : SellTradeIntent
    data object BackToEntry : SellTradeIntent
    data class ChangeEntryMode(val mode: SellTradeEntryMode) : SellTradeIntent
    data class ContinueToPayout(val grams: Double, val sellRateId: String, val estimatedPayoutAmount: Double) : SellTradeIntent
    data class SelectVpa(val vpaId: String) : SellTradeIntent
    data class ConfirmSell(val vpaId: String) : SellTradeIntent
    data class StartPolling(val orderId: String) : SellTradeIntent
}

sealed interface SellTradeEffect : MviEffect {
    data class ShowMessage(val message: String) : SellTradeEffect
}
