package com.habit.gold.feature.trade.presentation.sell

import androidx.lifecycle.viewModelScope
import com.habit.gold.core.network.ApiResult
import com.habit.gold.core.presentation.mvi.MviViewModel
import com.habit.gold.feature.trade.domain.model.TradePollingOutcome
import com.habit.gold.feature.trade.domain.model.TradePollingPolicies
import com.habit.gold.feature.trade.domain.usecase.CreateSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.ExecuteSellOrderUseCase
import com.habit.gold.feature.trade.domain.usecase.GetSellAvailabilityUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeStatusUseCase
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.PollTradeStatusUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.trade_sell_draft_missing
import kotlinx.coroutines.launch

class SellTradeViewModel(
    private val getSellAvailabilityUseCase: GetSellAvailabilityUseCase,
    private val getTradeUserVpasUseCase: GetTradeUserVpasUseCase,
    private val createSellOrderUseCase: CreateSellOrderUseCase,
    private val executeSellOrderUseCase: ExecuteSellOrderUseCase,
    private val pollTradeStatusUseCase: PollTradeStatusUseCase,
    private val getTradeStatusUseCase: GetTradeStatusUseCase,
) : MviViewModel<SellTradeState, SellTradeIntent, SellTradeEffect>(SellTradeState()) {

    private var hasLoaded = false

    override fun onIntent(intent: SellTradeIntent) {
        when (intent) {
            SellTradeIntent.Load -> {
                if (!hasLoaded) {
                    hasLoaded = true
                    loadFoundation()
                }
            }
            SellTradeIntent.BackToEntry -> updateState {
                it.copy(
                    step = SellTradeStep.Entry,
                    draftRequest = null,
                    createdOrder = null,
                    pollingSnapshot = null,
                    errorMessage = null,
                    errorMessageResource = null,
                )
            }
            is SellTradeIntent.ChangeEntryMode -> updateState { it.copy(entryMode = intent.mode) }
            is SellTradeIntent.ContinueToPayout -> updateState {
                it.copy(
                    step = SellTradeStep.PayoutVpa,
                    draftRequest = SellTradeDraftRequest(
                        grams = intent.grams,
                        sellRateId = intent.sellRateId,
                        estimatedPayoutAmount = intent.estimatedPayoutAmount,
                    ),
                    createdOrder = null,
                    errorMessage = null,
                )
            }
            is SellTradeIntent.SelectVpa -> updateState { it.copy(selectedVpaId = intent.vpaId) }
            is SellTradeIntent.ConfirmSell -> submitSell(intent.vpaId)
            is SellTradeIntent.StartPolling -> startPolling(intent.orderId)
        }
    }

    private fun loadFoundation() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null, errorMessageResource = null) }

            val availabilityResult = getSellAvailabilityUseCase()
            val vpaResult = getTradeUserVpasUseCase()

            val availability = (availabilityResult as? ApiResult.Success)?.value
            val vpas = (vpaResult as? ApiResult.Success)?.value.orEmpty()
            val preferredVpa = vpas.firstOrNull { it.isDefault } ?: vpas.firstOrNull { it.isVerified } ?: vpas.firstOrNull()

            val errorMessage = when {
                availabilityResult is ApiResult.Failure -> availabilityResult.error.message
                vpaResult is ApiResult.Failure -> vpaResult.error.message
                else -> null
            }

            updateState {
                it.copy(
                    isLoading = false,
                    availability = availability,
                    userVpas = vpas,
                    selectedVpaId = preferredVpa?.id,
                    errorMessage = errorMessage,
                    errorMessageResource = null,
                )
            }
        }
    }

    private fun submitSell(vpaId: String) {
        val draft = state.value.draftRequest
        if (draft == null) {
            updateState {
                it.copy(
                    errorMessage = null,
                    errorMessageResource = Res.string.trade_sell_draft_missing,
                )
            }
            return
        }
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null, errorMessageResource = null) }
            when (val createResult = createSellOrderUseCase(com.habit.gold.feature.trade.domain.model.TradeSellOrderRequest(draft.grams, draft.sellRateId))) {
                is ApiResult.Success -> {
                    val createdOrder = createResult.value
                    updateState { it.copy(createdOrder = createdOrder) }
                    when (val executeResult = executeSellOrderUseCase(createdOrder.orderId, vpaId)) {
                        is ApiResult.Success -> {
                            updateState { it.copy(isLoading = false, errorMessage = null, errorMessageResource = null) }
                            startPolling(orderId = executeResult.value.orderId.ifBlank { createdOrder.orderId })
                        }
                        is ApiResult.Failure -> updateState {
                            it.copy(
                                isLoading = false,
                                createdOrder = createdOrder,
                                errorMessage = executeResult.error.message,
                                errorMessageResource = null,
                            )
                        }
                    }
                }
                is ApiResult.Failure -> updateState {
                    it.copy(
                        isLoading = false,
                        errorMessage = createResult.error.message,
                        errorMessageResource = null,
                    )
                }
            }
        }
    }

    private fun startPolling(orderId: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null, errorMessageResource = null) }
            when (val result = pollTradeStatusUseCase(orderId, TradePollingPolicies.sell())) {
                is ApiResult.Success -> {
                    val nextSnapshot = when (val outcome = result.value) {
                        is TradePollingOutcome.Success -> outcome.snapshot
                        is TradePollingOutcome.Failure -> outcome.snapshot
                        is TradePollingOutcome.Processing -> outcome.snapshot
                        is TradePollingOutcome.Pending -> outcome.snapshot
                    }
                    val nextStep = when (val outcome = result.value) {
                        is TradePollingOutcome.Success -> SellTradeStep.Success
                        is TradePollingOutcome.Failure -> SellTradeStep.Failure
                        is TradePollingOutcome.Pending -> SellTradeStep.Pending
                        is TradePollingOutcome.Processing -> SellTradeStep.Pending
                    }
                    val nextError = (result.value as? TradePollingOutcome.Failure)?.message
                    updateState {
                        it.copy(
                            isLoading = false,
                            step = nextStep,
                            pollingSnapshot = nextSnapshot,
                            errorMessage = nextError,
                            errorMessageResource = null,
                        )
                    }
                }
                is ApiResult.Failure -> {
                    val latestStatus = getTradeStatusUseCase(orderId)
                    val snapshot = (latestStatus as? ApiResult.Success)?.value?.let { status ->
                        com.habit.gold.feature.trade.domain.model.TradePollingSnapshot(
                            orderId = status.orderId,
                            status = status.status.uppercase(),
                            attempt = TradePollingPolicies.sell().maxAttempts,
                        )
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            step = SellTradeStep.Pending,
                            pollingSnapshot = snapshot,
                            errorMessage = result.error.message,
                            errorMessageResource = null,
                        )
                    }
                }
            }
        }
    }
}
