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
import com.habit.gold.core.presentation.CommonCouponCard
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import androidx.compose.ui.platform.LocalFocusManager
import com.habit.gold.feature.trade.domain.model.TradeCouponType
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
    val focusManager = LocalFocusManager.current
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
    var showCouponSheet by remember { mutableStateOf(false) }

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
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
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
            CommonCouponCard(
                couponDraft = localCouponCode,
                appliedCouponCode = catalogState.couponCode,
                appliedBenefitText = catalogState.couponType?.let {
                    when (it) {
                        TradeCouponType.FREE_DELIVERY -> stringResource(Res.string.delivery_cart_free_delivery_applied)
                        else -> stringResource(Res.string.delivery_cart_coupon_applied)
                    }
                },
                onCouponDraftChange = { localCouponCode = it },
                onApplyCoupon = { onIntent(DeliveryIntent.ApplyCoupon(localCouponCode)) },
                onRemoveAppliedCoupon = {
                    localCouponCode = ""
                    onIntent(DeliveryIntent.RemoveCoupon)
                },
                onShowOffers = { showCouponSheet = true },
                isApplyingEnabled = localCouponCode.isNotBlank(),
                availableCoupons = catalogState.availableCoupons.size,
                onDone = { focusManager.clearFocus() }
            )

            PaymentDetailsCard(
                totalGoldAvailableGrams = catalogState.redeemableGoldGrams,
                goldDebitGrams = checkoutQuote?.goldWeightGrams ?: (selectedCoin.weightGm * (catalogState.cartItems[selectedCoin.id] ?: 1)),
                mintingChargeInr = checkoutQuote?.mintingChargeInr ?: (selectedCoin.makingCharge * (catalogState.cartItems[selectedCoin.id] ?: 1)),
                couponDiscountInr = checkoutQuote?.let {
                    (it.mintingChargeInr - it.payableChargeInr).coerceAtLeast(0.0)
                } ?: catalogState.couponDiscountInr,
                netAmountPayable = payableAmount,
                couponType = catalogState.couponType
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

    if (showCouponSheet) {
        val couponSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCouponSheet = false },
            sheetState = couponSheetState,
            containerColor = AppColors.White
        ) {
            DeliveryCouponSheet(
                coupons = catalogState.availableCoupons,
                appliedCouponCode = catalogState.couponCode,
                onApplyCoupon = { code ->
                    localCouponCode = code
                    onIntent(DeliveryIntent.ApplyCoupon(code))
                    showCouponSheet = false
                }
            )
        }
    }
}



@Composable
private fun CoinHeadlineCard(
    coin: PhysicalCoin,
    totalGoldAvailableGrams: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate125)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = AppColors.Slate50,
                    modifier = Modifier.size(96.dp)
                ) {
                    AsyncImage(
                        model = coin.imageUrl,
                        contentDescription = coin.productName,
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = coin.productName,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate950
                    )
                    Text(
                        text = coin.metalStamp,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate500
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = AppColors.Primary.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = stringResource(Res.string.delivery_cart_total_gold_available, formatGrams(totalGoldAvailableGrams)),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentDetailsCard(
    totalGoldAvailableGrams: Double,
    goldDebitGrams: Double,
    mintingChargeInr: Double,
    couponDiscountInr: Double,
    netAmountPayable: Double
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate125)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(Res.string.delivery_cart_payment_details),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate950
            )

            PaymentLine(
                label = stringResource(Res.string.delivery_cart_grams_available),
                value = "${formatGrams(totalGoldAvailableGrams)}g",
                valueColor = AppColors.Green700
            )
            PaymentLine(
                label = stringResource(Res.string.delivery_cart_gold_used),
                value = "${formatGrams(goldDebitGrams)}g",
                valueColor = AppColors.Neutral400
            )
            PaymentLine(
                label = stringResource(Res.string.delivery_cart_delivery_charges),
                value = "₹${formatAmount(mintingChargeInr)}"
            )
            if (couponDiscountInr > 0.0) {
                PaymentLine(
                    label = stringResource(Res.string.delivery_cart_discount),
                    value = "-₹${formatAmount(couponDiscountInr)}",
                    valueColor = AppColors.Green600
                )
            }
            PaymentLine(
                label = stringResource(Res.string.delivery_cart_insurance_charge),
                value = stringResource(Res.string.delivery_cart_free)
            )

            HorizontalDivider(color = AppColors.Slate100, thickness = 1.dp)

            PaymentLine(
                label = stringResource(Res.string.delivery_cart_net_payable),
                value = "₹${formatAmount(netAmountPayable)}",
                emphasize = true
            )
        }
    }
}

@Composable
private fun PaymentLine(
    label: String,
    value: String,
    emphasize: Boolean = false,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (emphasize) 15.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) AppColors.Slate950 else AppColors.Slate500
        )
        Text(
            text = value,
            fontSize = if (emphasize) 20.sp else 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor ?: if (emphasize) AppColors.Primary else AppColors.Slate950
        )
    }
}

@Composable
private fun SecureOrderNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Amber50,
        border = BorderStroke(1.dp, AppColors.Amber200)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AppColors.Gold600,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = stringResource(Res.string.delivery_cart_gold_balance_covers),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Amber800
            )
        }
    }
}

@Composable
private fun CartSummaryBottomBar(
    selectedAddress: SavedAddress?,
    addressServiceable: Boolean,
    isLoadingAddresses: Boolean,
    addressLocked: Boolean,
    onAddressClick: () -> Unit,
    onProceedClick: () -> Unit,
    ctaEnabled: Boolean,
    ctaLabel: String,
    isLoadingPayment: Boolean
) {
    Surface(
        color = AppColors.White,
        shadowElevation = 10.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AddressStrip(
                selectedAddress = selectedAddress,
                addressServiceable = addressServiceable,
                isLoadingAddresses = isLoadingAddresses,
                addressLocked = addressLocked,
                onClick = onAddressClick
            )

            Button(
                onClick = onProceedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = ctaEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 16.dp,
                    disabledElevation = 0.dp
                )
            ) {
                if (isLoadingPayment) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AppColors.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = ctaLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressStrip(
    selectedAddress: SavedAddress?,
    addressServiceable: Boolean,
    isLoadingAddresses: Boolean,
    addressLocked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !addressLocked, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Slate50,
        border = BorderStroke(1.dp, AppColors.Slate200)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                when {
                    isLoadingAddresses -> {
                        Text(
                            text = stringResource(Res.string.delivery_cart_loading_address),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Slate950
                        )
                    }
                    selectedAddress == null -> {
                        Text(
                            text = stringResource(Res.string.delivery_cart_add_delivery_address),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate950
                        )
                        Text(
                            text = stringResource(Res.string.delivery_cart_select_where_to_deliver),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = AppColors.Slate500
                        )
                    }
                    else -> {
                        Text(
                            text = selectedAddress.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Slate950,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = selectedAddress.compactAddressLine(),
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (addressServiceable) {
                                if (addressLocked) "Address locked for this active quote" else "Estimated Delivery 7-14 Days"
                            } else {
                                "Choose a serviceable address"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (addressServiceable) AppColors.Green700 else AppColors.Orange700
                        )
                    }
                }
            }

            if (isLoadingAddresses) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.Primary
                )
            } else if (!addressLocked) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = AppColors.Slate400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfirmPaymentDialog(
    address: SavedAddress,
    addressServiceable: Boolean,
    checkoutQuote: DeliveryCheckoutQuote,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            color = AppColors.White,
            border = BorderStroke(1.dp, AppColors.Purple300),
            shadowElevation = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AppColors.Purple100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = stringResource(Res.string.delivery_cart_review_order),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Purple950
                                )
                                Text(
                                    text = stringResource(Res.string.delivery_cart_quote_ready),
                                    fontSize = 12.sp,
                                    color = AppColors.PurpleGray600,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, enabled = !isSubmitting) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AppColors.PurpleGray600
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = AppColors.Purple50,
                        border = BorderStroke(1.dp, AppColors.Purple200)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = address.name,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Purple950,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = if (addressServiceable) AppColors.Purple200 else AppColors.Red100
                                ) {
                                    Text(
                                        text = stringResource(if (addressServiceable) Res.string.delivery_cart_verified else Res.string.delivery_cart_unavailable),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (addressServiceable) AppColors.Primary else AppColors.Red700
                                    )
                                }
                            }

                            Text(
                                text = address.compactAddressLine(),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.PurpleGray700,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = RoundedCornerShape(999.dp), color = AppColors.White) {
                                    Text(
                                        text = address.phoneNo,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.Purple900
                                    )
                                }
                                address.pincode?.takeIf { it.isNotBlank() }?.let { pincode ->
                                    Surface(shape = RoundedCornerShape(999.dp), color = AppColors.White) {
                                        Text(
                                            text = pincode,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AppColors.Purple900
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = AppColors.Neutral25,
                        border = BorderStroke(1.dp, AppColors.Slate200Alt)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PaymentLine(
                                label = stringResource(Res.string.delivery_cart_gold_used),
                                value = "${formatGrams(checkoutQuote.goldWeightGrams)}g",
                                valueColor = AppColors.Violet600
                            )
                            PaymentLine(
                                label = stringResource(Res.string.delivery_cart_delivery_charges),
                                value = "₹${formatAmount(checkoutQuote.mintingChargeInr)}"
                            )
                            PaymentLine(
                                label = stringResource(Res.string.delivery_cart_insurance_charge),
                                value = stringResource(Res.string.delivery_cart_free),
                                valueColor = AppColors.Green700
                            )
                            checkoutQuote.estimatedDispatchDays?.let { dispatchDays ->
                                PaymentLine(
                                    label = stringResource(Res.string.delivery_cart_est_dispatch),
                                    value = stringResource(Res.string.delivery_cart_days, dispatchDays)
                                )
                            }
                            checkoutQuote.verifyExpiresAt?.let { expiresAt ->
                                PaymentLine(
                                    label = stringResource(Res.string.delivery_cart_quote_valid),
                                    value = formatExpiresAt(expiresAt)
                                )
                            }
                            HorizontalDivider(color = AppColors.Slate200Alt, thickness = 1.dp)
                            PaymentLine(
                                label = stringResource(Res.string.delivery_cart_payable_now),
                                value = "₹${formatAmount(checkoutQuote.payableChargeInr)}",
                                emphasize = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = addressServiceable && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = AppColors.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (checkoutQuote.payableChargeInr > 0.0) {
                                stringResource(Res.string.delivery_cart_pay_amount, formatAmount(checkoutQuote.payableChargeInr))
                            } else {
                                stringResource(Res.string.delivery_cart_confirm_order)
                            },
                            fontWeight = FontWeight.Bold,
                            color = AppColors.White
                        )
                    }
                }
            }
        }
    }
}


