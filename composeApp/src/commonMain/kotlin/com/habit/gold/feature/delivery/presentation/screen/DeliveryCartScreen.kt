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

@Composable
private fun CouponCodeCard(
    couponCode: String,
    appliedCoupon: String?,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.White,
        border = BorderStroke(1.dp, AppColors.Slate125)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(Res.string.delivery_cart_coupon_code),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate950
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (appliedCoupon != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(AppColors.Green50, RoundedCornerShape(12.dp)).border(1.dp, AppColors.Green200, RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AppColors.Green600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = appliedCoupon, fontWeight = FontWeight.Bold, color = AppColors.Green800)
                    }
                    TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(Res.string.delivery_cart_remove), color = AppColors.Red600, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = { onCodeChange(it.uppercase()) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter coupon code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Primary,
                            unfocusedBorderColor = AppColors.Slate200
                        )
                    )
                    Button(
                        onClick = onApply,
                        enabled = couponCode.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(stringResource(Res.string.delivery_cart_apply), fontWeight = FontWeight.Bold)
                    }
                }
            }
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

/** Shows gold weight with exactly 4 decimal places, e.g. 0.5000, 1.0000. */
private fun formatGrams(value: Double): String {
    val scaled = (value * 10000).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 10000
    val frac = (scaled % 10000).let { if (it < 0) -it else it }
    return "${whole}.${frac.toString().padStart(4, '0')}"
}

/** Shows INR amount with exactly 2 decimal places and US grouping, e.g. 150.00, 1,250.50. */
private fun formatAmount(value: Double): String {
    val scaled = (value * 100).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 100
    val frac = (scaled % 100).let { if (it < 0) -it else it }
    
    val wholeStr = whole.toString()
    val withCommas = buildString {
        val len = wholeStr.length
        for (i in wholeStr.indices) {
            append(wholeStr[i])
            val remaining = len - 1 - i
            if (remaining > 0 && remaining % 3 == 0) {
                append(',')
            }
        }
    }
    return "${withCommas}.${frac.toString().padStart(2, '0')}"
}

/**
 * Parses an ISO-8601 timestamp or epoch milliseconds and returns
 * a human-readable string like "18 May, 01:20 AM".
 * Falls back to the raw string if parsing fails.
 */
internal fun formatExpiresAt(raw: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return try {
        val asLong = raw.toLongOrNull()
        val instant = if (asLong != null) {
            val ms = if (raw.length <= 10) asLong * 1000 else asLong
            Instant.fromEpochMilliseconds(ms)
        } else {
            Instant.parse(raw)
        }
        val local = instant.toLocalDateTime(timeZone)
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthName = monthNames.getOrNull(local.month.ordinal) ?: "M"
        val amPm = if (local.hour < 12) "AM" else "PM"
        val displayHour = when {
            local.hour == 0 -> 12
            local.hour > 12 -> local.hour - 12
            else -> local.hour
        }
        val minuteStr = local.minute.toString().padStart(2, '0')
        val dayStr = local.day.toString().padStart(2, '0')
        val hourStr = displayHour.toString().padStart(2, '0')
        "$dayStr $monthName, $hourStr:$minuteStr $amPm"
    } catch (e: Exception) {
        raw
    }
}
