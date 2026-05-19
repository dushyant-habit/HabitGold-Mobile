package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogState
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.DeliveryIntent
import com.habit.gold.feature.delivery.presentation.components.*
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryCartScreen(
    catalogState: DeliveryCatalogState,
    addressState: DeliveryAddressState,
    onIntent: (DeliveryIntent) -> Unit,
    onBackClick: () -> Unit,
    onChangeAddressClick: () -> Unit,
    onBackToDashboard: () -> Unit = {}
) {
    val selectedCoinId = catalogState.cartItems.keys.firstOrNull()
    val productId = catalogState.cartItems.keys.minOrNull()
    val selectedCoin = catalogState.coins.find { it.id == selectedCoinId }
    val selectedAddress = addressState.savedAddresses.find { it.id == catalogState.selectedAddressId }
    
    val addressServiceable = selectedAddress?.isPincodeServiceable() == true
    
    val canProceed = selectedCoin != null && selectedAddress != null && addressServiceable
    val checkoutQuote = catalogState.checkoutQuote
    val payableAmount = checkoutQuote?.payableChargeInr ?: catalogState.netAmountPayable

    var showConfirmDialog by remember { mutableStateOf(false) }
    var localCouponCode by remember { mutableStateOf(catalogState.couponCode ?: "") }

    LaunchedEffect(catalogState.couponCode) {
        if (catalogState.couponCode != null) {
            localCouponCode = catalogState.couponCode
        }
    }

    if (catalogState.checkoutPhase == com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.VERIFYING_ORDER) {
        PaymentVerificationScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.delivery_cart_summary),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate950
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onIntent(DeliveryIntent.DiscardCheckout)
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = AppColors.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.White,
                    scrolledContainerColor = AppColors.White,
                    navigationIconContentColor = AppColors.Black,
                    titleContentColor = AppColors.Black,
                    actionIconContentColor = AppColors.Black
                )
            )
        },
        bottomBar = {
            CartSummaryBottomBar(
                selectedAddress = selectedAddress,
                addressServiceable = addressServiceable,
                isLoadingAddresses = addressState.isLoadingAddresses,
                addressLocked = catalogState.isAddressEditingLocked,
                onAddressClick = onChangeAddressClick,
                onProceedClick = {
                    val phase = catalogState.checkoutPhase
                    val alreadyInFlight = phase == com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.CONFIRMING ||
                        phase == com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.PAYMENT_LAUNCH_READY ||
                        phase == com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.PAYMENT_IN_PROGRESS ||
                        phase == com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase.VERIFYING_ORDER
                    if (selectedAddress != null && productId != null && !alreadyInFlight) {
                        onIntent(DeliveryIntent.PrepareQuote(catalogState.couponCode))
                        showConfirmDialog = true
                    }
                },
                ctaEnabled = canProceed && !catalogState.isCheckingOut,
                ctaLabel = when {
                    selectedAddress == null -> stringResource(Res.string.delivery_cart_add_address)
                    !addressServiceable -> stringResource(Res.string.delivery_cart_select_serviceable)
                    payableAmount > 0.0 -> stringResource(
                        Res.string.delivery_cart_review_and_pay_amount,
                        formatAmount(payableAmount)
                    )
                    else -> stringResource(Res.string.delivery_cart_review_delivery)
                },
                isLoadingPayment = catalogState.isCheckingOut
            )
        },
        containerColor = AppColors.Slate10
    ) { paddingValues ->
        if (selectedCoin == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(Res.string.delivery_cart_empty_cart),
                    fontSize = 15.sp,
                    color = AppColors.Slate500
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CoinHeadlineCard(
                coin = selectedCoin,
                totalGoldAvailableGrams = catalogState.redeemableGoldGrams
            )
            
            // Coupon Code UI
            CouponCodeCard(
                couponCode = localCouponCode,
                appliedCoupon = catalogState.couponCode,
                onCodeChange = { localCouponCode = it },
                onApply = { onIntent(DeliveryIntent.ApplyCoupon(localCouponCode)) },
                onRemove = {
                    localCouponCode = ""
                    onIntent(DeliveryIntent.RemoveCoupon)
                }
            )

            PaymentDetailsCard(
                totalGoldAvailableGrams = catalogState.redeemableGoldGrams,
                goldDebitGrams = checkoutQuote?.goldWeightGrams ?: (selectedCoin.weightGm * (catalogState.cartItems[selectedCoin.id] ?: 1)),
                mintingChargeInr = checkoutQuote?.mintingChargeInr ?: (selectedCoin.makingCharge * (catalogState.cartItems[selectedCoin.id] ?: 1)),
                couponDiscountInr = checkoutQuote?.let {
                    (it.mintingChargeInr - it.payableChargeInr).coerceAtLeast(0.0)
                } ?: catalogState.couponDiscountInr,
                netAmountPayable = payableAmount
            )

            SecureOrderNote()

            Spacer(modifier = Modifier.height(92.dp))
        }
    }

    if (showConfirmDialog && selectedAddress != null && checkoutQuote != null) {
        ConfirmPaymentDialog(
            address = selectedAddress,
            addressServiceable = addressServiceable,
            checkoutQuote = checkoutQuote,
            isSubmitting = catalogState.isCheckingOut,
            onDismiss = {
                showConfirmDialog = false
                onIntent(DeliveryIntent.DiscardCheckout)
            },
            onConfirm = {
                showConfirmDialog = false
                onIntent(DeliveryIntent.ConfirmOrder)
            }
        )
    }
}

