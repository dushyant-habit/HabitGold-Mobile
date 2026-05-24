package com.habit.gold.feature.delivery.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.DeliveryPaymentLaunchRequest
import com.habit.gold.feature.delivery.presentation.screen.AddEditAddressScreen
import com.habit.gold.feature.delivery.presentation.screen.DeliveryAddressScreen
import com.habit.gold.feature.delivery.presentation.screen.DeliveryCartScreen
import com.habit.gold.feature.delivery.presentation.screen.DeliveryCatalogScreen
import com.habit.gold.feature.delivery.presentation.screen.DeliveryOrderSummaryScreen
import com.habit.gold.feature.delivery.presentation.screen.DeliveryTrackingScreen

sealed interface DeliveryDestination {
    data object Catalog : DeliveryDestination
    data object Cart : DeliveryDestination
    data object AddressList : DeliveryDestination
    data object AddEditAddress : DeliveryDestination
    data class OrderSummary(val orderId: String) : DeliveryDestination
    data object Tracking : DeliveryDestination
}

@Composable
fun DeliveryRoute(
    dependencies: DeliveryRouteDependencies,
    catalogViewModel: DeliveryCatalogViewModel,
    trackingViewModel: DeliveryTrackingViewModel,
    initialDestination: DeliveryDestination = DeliveryDestination.Catalog,
    onBackToHome: () -> Unit,
    onNavigateToBuyGold: (shortfallGrams: Double) -> Unit,
    onGoToDashboard: (() -> Unit)? = null,
) {

    val addressViewModel = viewModel {
        DeliveryAddressViewModel(
            listUserAddressesUseCase = dependencies.listUserAddressesUseCase,
            createUserAddressUseCase = dependencies.createUserAddressUseCase,
            updateUserAddressUseCase = dependencies.updateUserAddressUseCase,
            deleteUserAddressUseCase = dependencies.deleteUserAddressUseCase,
            sendAddressOtpUseCase = dependencies.sendAddressOtpUseCase,
            verifyAddressOtpUseCase = dependencies.verifyAddressOtpUseCase,
            checkAddressServiceabilityUseCase = dependencies.checkAddressServiceabilityUseCase,
            validateDeliveryPincodeUseCase = dependencies.validateDeliveryPincodeUseCase,
            lookupPostalPincodeUseCase = dependencies.lookupPostalPincodeUseCase,
            sessionStore = dependencies.sessionStore,
        )
    }

    val catalogState by catalogViewModel.state.collectAsState()
    val addressState by addressViewModel.state.collectAsState()
    val trackingState by trackingViewModel.state.collectAsState()

    val paymentLauncher = rememberPlatformDeliveryPaymentLauncher()
    var destination by remember { mutableStateOf<DeliveryDestination>(initialDestination) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(catalogViewModel.effects) {
        catalogViewModel.effects.collect { effect ->
            when (effect) {
                is DeliveryEffect.NavigateToCart -> destination = DeliveryDestination.Cart
                is DeliveryEffect.NavigateToCheckout -> destination = DeliveryDestination.Cart
                is DeliveryEffect.NavigateToOrderSummary -> {
                    destination = DeliveryDestination.OrderSummary(effect.orderId)
                }
                is DeliveryEffect.NavigateBack -> destination = DeliveryDestination.Catalog
                is DeliveryEffect.NavigateToBuyGold -> onNavigateToBuyGold(effect.shortfallGrams)
                is DeliveryEffect.LaunchPaymentSdk -> {
                    val request = DeliveryPaymentLaunchRequest.Juspay(
                        payloadJson = effect.payloadJson,
                        preferredUpiPackage = null,
                    )
                    val result = paymentLauncher.launch(request)
                    catalogViewModel.onIntent(DeliveryIntent.HandlePaymentResult(result))
                }
                is DeliveryEffect.OrderCompleted -> {
                    destination = DeliveryDestination.OrderSummary(effect.orderId)
                }
                is DeliveryEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.resolveSuspending())
                }
                is DeliveryEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message.resolveSuspending())
                }
            }
        }
    }

    LaunchedEffect(addressViewModel.effects) {
        addressViewModel.effects.collect { effect ->
            when (effect) {
                is DeliveryAddressEffect.AddressSaved -> destination = DeliveryDestination.AddressList
                is DeliveryAddressEffect.AddressFullyVerified -> destination = DeliveryDestination.AddressList
                is DeliveryAddressEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.resolveSuspending())
                }
                is DeliveryAddressEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message.resolveSuspending())
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = AppColors.Slate900,
                    contentColor = AppColors.White,
                )
            }
        },
        containerColor = AppColors.White,
    ) { _ ->
        when (val dest = destination) {
            is DeliveryDestination.Catalog -> DeliveryCatalogScreen(
                state = catalogState,
                onIntent = catalogViewModel::onIntent,
                onBackClick = onBackToHome,
                onNavigateToCart = { destination = DeliveryDestination.Cart },
                onNavigateToBuyGold = onNavigateToBuyGold,
            )

            is DeliveryDestination.Cart -> DeliveryCartScreen(
                catalogState = catalogState,
                addressState = addressState,
                onIntent = catalogViewModel::onIntent,
                onBackClick = { destination = DeliveryDestination.Catalog },
                onChangeAddressClick = { destination = DeliveryDestination.AddressList },
                onBackToDashboard = onBackToHome,
            )

            is DeliveryDestination.AddressList -> DeliveryAddressScreen(
                state = addressState,
                selectedAddressId = catalogState.selectedAddressId,
                onIntent = addressViewModel::onIntent,
                onSelectAddress = { catalogViewModel.onIntent(DeliveryIntent.SelectAddress(it)) },
                onBackClick = {
                    if (initialDestination is DeliveryDestination.AddressList) {
                        onBackToHome()
                    } else {
                        destination = DeliveryDestination.Cart
                    }
                },
                onAddNewAddress = { destination = DeliveryDestination.AddEditAddress },
                onEditAddress = { destination = DeliveryDestination.AddEditAddress },
                onContinue = { destination = DeliveryDestination.Cart },
                showCheckoutButton = initialDestination !is DeliveryDestination.AddressList,
            )

            is DeliveryDestination.AddEditAddress -> AddEditAddressScreen(
                state = addressState,
                onIntent = addressViewModel::onIntent,
                onBackClick = { destination = DeliveryDestination.AddressList },
            )

            is DeliveryDestination.OrderSummary -> DeliveryOrderSummaryScreen(
                catalogState = catalogState,
                addressState = addressState,
                orderId = dest.orderId,
                onIntent = catalogViewModel::onIntent,
                onBackClick = { destination = DeliveryDestination.Catalog },
                onTrackOrder = { destination = DeliveryDestination.Tracking },
                onDone = onGoToDashboard ?: onBackToHome,
            )

            is DeliveryDestination.Tracking -> DeliveryTrackingScreen(
                state = trackingState,
                onIntent = trackingViewModel::onIntent,
                onBackClick = {
                    if (initialDestination is DeliveryDestination.Tracking) {
                        onBackToHome()
                    } else {
                        destination = DeliveryDestination.Catalog
                    }
                },
            )
        }
    }
}
