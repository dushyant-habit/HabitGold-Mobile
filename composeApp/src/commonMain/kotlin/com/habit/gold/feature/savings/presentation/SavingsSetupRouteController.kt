package com.habit.gold.feature.savings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habit.gold.feature.trade.domain.TradeLivePriceStore
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.trade.domain.TradePaymentLauncher

@Composable
internal fun SavingsSetupRouteController(
    destination: SavingsDestination.Setup,
    viewModel: SavingsSetupViewModel,
    livePriceStore: TradeLivePriceStore,
    paymentLauncher: TradePaymentLauncher,
    onBackToHome: () -> Unit,
    onSavingsMutation: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val livePriceState = livePriceStore.state.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        viewModel.onIntent(SavingsSetupIntent.Initialize(destination))
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SavingsSetupEffect.LaunchPayment -> {
                    val result = paymentLauncher.launch(effect.request)
                    viewModel.onIntent(SavingsSetupIntent.HandlePaymentResult(result))
                }
            }
        }
    }

    PlatformBackHandler(
        enabled = state.value.phase !is SavingsSetupPhase.Polling,
        onBack = {
            when (state.value.phase) {
                SavingsSetupPhase.Form -> onBackToHome()
                else -> {
                    viewModel.onIntent(SavingsSetupIntent.ResetToForm)
                    onBackToHome()
                }
            }
        },
    )

    SavingsSetupScreen(
        state = state.value,
        livePriceState = livePriceState.value,
        onBackClick = {
            when (state.value.phase) {
                SavingsSetupPhase.Form -> onBackToHome()
                else -> {
                    viewModel.onIntent(SavingsSetupIntent.ResetToForm)
                    onBackToHome()
                }
            }
        },
        onHelpClick = onOpenHelp,
        onAmountChange = { viewModel.onIntent(SavingsSetupIntent.ChangeAmount(it)) },
        onExecutionDaySelect = { viewModel.onIntent(SavingsSetupIntent.SelectExecutionDay(it)) },
        onQuickAmountSelected = { amount ->
            viewModel.onIntent(SavingsSetupIntent.ChangeAmount(amount.toString()))
        },
        onCouponDraftChange = { viewModel.onIntent(SavingsSetupIntent.ChangeCouponDraft(it)) },
        onApplyCoupon = { code -> viewModel.onIntent(SavingsSetupIntent.ApplyCoupon(code)) },
        onRemoveAppliedCoupon = { viewModel.onIntent(SavingsSetupIntent.ClearAppliedCoupon) },
        onSubmit = { viewModel.onIntent(SavingsSetupIntent.Submit) },
        onRetryPolling = { viewModel.onIntent(SavingsSetupIntent.RetryPolling) },
        onGoHome = {
            onSavingsMutation()
            viewModel.onIntent(SavingsSetupIntent.ResetToForm)
            onBackToHome()
        },
        modifier = modifier,
    )
}
