package com.habit.gold.feature.savings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.presentation.PlatformBackHandler
import com.habit.gold.feature.savings.domain.usecase.CreateSavingsMandateSessionUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.CancelSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.GetSavingsMandatesUseCase
import com.habit.gold.feature.savings.domain.usecase.PauseSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.ResumeSavingsMandateUseCase
import com.habit.gold.feature.savings.domain.usecase.UpdateSavingsMandateSessionUseCase
import com.habit.gold.feature.trade.domain.TradeLivePriceStore
import com.habit.gold.feature.trade.domain.TradePaymentLauncher
import com.habit.gold.feature.trade.domain.usecase.GetTradeAvailableCouponsUseCase
import com.habit.gold.feature.trade.domain.usecase.ValidateTradeCouponUseCase

data class SavingsRouteDependencies(
    val getSavingsMandatesUseCase: GetSavingsMandatesUseCase,
    val getSavingsMandateUseCase: GetSavingsMandateUseCase,
    val pauseSavingsMandateUseCase: PauseSavingsMandateUseCase,
    val resumeSavingsMandateUseCase: ResumeSavingsMandateUseCase,
    val cancelSavingsMandateUseCase: CancelSavingsMandateUseCase,
    val createSavingsMandateSessionUseCase: CreateSavingsMandateSessionUseCase,
    val updateSavingsMandateSessionUseCase: UpdateSavingsMandateSessionUseCase,
    val getTradeAvailableCouponsUseCase: GetTradeAvailableCouponsUseCase,
    val validateTradeCouponUseCase: ValidateTradeCouponUseCase,
    val paymentLauncher: TradePaymentLauncher,
    val livePriceStore: TradeLivePriceStore,
)

@Composable
fun SavingsRoute(
    dependencies: SavingsRouteDependencies,
    sessionResetKey: String,
    destination: SavingsDestination,
    onBackToHome: () -> Unit,
    onSavingsMutation: () -> Unit = {},
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (destination) {
        SavingsDestination.Manage -> {
            val viewModel = viewModel(key = "savings-manage:$sessionResetKey") {
                SavingsViewModel(
                    getSavingsMandatesUseCase = dependencies.getSavingsMandatesUseCase,
                    pauseSavingsMandateUseCase = dependencies.pauseSavingsMandateUseCase,
                    resumeSavingsMandateUseCase = dependencies.resumeSavingsMandateUseCase,
                    cancelSavingsMandateUseCase = dependencies.cancelSavingsMandateUseCase,
                )
            }
            val state = viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state.value.actionMessage) {
                if (state.value.actionMessage != null) {
                    onSavingsMutation()
                }
            }

            PlatformBackHandler(
                enabled = true,
                onBack = onBackToHome,
            )

            SavingsScreen(
                state = state.value,
                onBackClick = onBackToHome,
                onRefresh = { viewModel.onIntent(SavingsIntent.Refresh) },
                onFilterSelected = { viewModel.onIntent(SavingsIntent.SelectFilter(it)) },
                onClearFilter = { viewModel.onIntent(SavingsIntent.ClearFilter) },
                onToggleExpanded = { viewModel.onIntent(SavingsIntent.ToggleExpanded(it)) },
                onPauseMandate = { viewModel.onIntent(SavingsIntent.PauseMandate(it)) },
                onResumeMandate = { viewModel.onIntent(SavingsIntent.ResumeMandate(it)) },
                onCancelMandate = { viewModel.onIntent(SavingsIntent.CancelMandate(it)) },
                onConsumeActionMessage = { viewModel.onIntent(SavingsIntent.ConsumeActionMessage) },
                modifier = modifier,
            )
        }

        is SavingsDestination.Setup -> {
            val viewModel = viewModel(key = "savings-setup:$sessionResetKey") {
                SavingsSetupViewModel(
                    createSavingsMandateSessionUseCase = dependencies.createSavingsMandateSessionUseCase,
                    updateSavingsMandateSessionUseCase = dependencies.updateSavingsMandateSessionUseCase,
                    getSavingsMandateUseCase = dependencies.getSavingsMandateUseCase,
                    getTradeAvailableCouponsUseCase = dependencies.getTradeAvailableCouponsUseCase,
                    validateTradeCouponUseCase = dependencies.validateTradeCouponUseCase,
                )
            }
            SavingsSetupRouteController(
                destination = destination,
                viewModel = viewModel,
                livePriceStore = dependencies.livePriceStore,
                paymentLauncher = dependencies.paymentLauncher,
                onBackToHome = onBackToHome,
                onSavingsMutation = onSavingsMutation,
                onOpenHelp = onOpenHelp,
                modifier = modifier,
            )
        }
    }
}
