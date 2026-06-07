package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogState
import com.habit.gold.feature.delivery.presentation.DeliveryAddressState
import com.habit.gold.feature.delivery.presentation.DeliveryIntent
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.habit.gold.core.util.formatGramsTruncate
import com.habit.gold.core.util.formatMoneyCeil
import com.habit.gold.core.util.formatMoney0

/**
 * Delivery Order Summary screen shown after payment is confirmed.
 *
 * Mirrors GetCoinOrderSummaryScreen from legacy Android:
 *  - Order ID, status chip
 *  - Delivery-to address snapshot
 *  - Coin details (product name, weight, making charge, discount, total)
 *  - "Track Order" and "Done" CTAs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryOrderSummaryScreen(
    catalogState: DeliveryCatalogState,
    addressState: DeliveryAddressState,
    orderId: String,
    onIntent: (DeliveryIntent) -> Unit,
    onBackClick: () -> Unit,
    onTrackOrder: () -> Unit,
    onDone: () -> Unit,
) {
    val quote: DeliveryCheckoutQuote? = catalogState.checkoutQuote
    val selectedAddress: SavedAddress? = addressState.savedAddresses.find { it.id == catalogState.selectedAddressId }
    val selectedCoin = quote?.productId?.let { id -> catalogState.coins.find { it.id == id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.order_summary_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate950,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = AppColors.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.White,
                    scrolledContainerColor = AppColors.White,
                    titleContentColor = AppColors.Slate950,
                    navigationIconContentColor = AppColors.Black,
                ),
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = AppColors.White,
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple700),
                    ) {
                        Text(
                            text = stringResource(Res.string.order_summary_done),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.White
                        )
                    }
                    OutlinedButton(
                        onClick = onTrackOrder,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppColors.Purple700),
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = AppColors.Purple700,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.order_summary_track),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Purple700,
                        )
                    }
                }
            }
        },
        containerColor = AppColors.SurfaceLight,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Success header
            SuccessHeaderCard()

            // Order ID
            SummaryInfoCard(
                label = stringResource(Res.string.order_summary_order_id),
                value = orderId,
            )

            // Delivery address
            if (selectedAddress != null) {
                SummarySection(title = stringResource(Res.string.order_summary_delivery_to)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppColors.Purple700,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp),
                        )
                        Column {
                            Text(
                                text = selectedAddress.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = AppColors.Slate900,
                            )
                            Text(
                                text = selectedAddress.compactAddressLine(),
                                fontSize = 13.sp,
                                color = AppColors.Slate600,
                            )
                        }
                    }
                }
            }

            // Coin details + pricing
            if (quote != null) {
                SummarySection(title = stringResource(Res.string.order_summary_coin_details)) {
                    if (selectedCoin != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(selectedCoin.productName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppColors.Slate900)
                            Text("${selectedCoin.weightGm} gm", fontSize = 13.sp, color = AppColors.Slate600)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                    }
                    PriceRow(
                        label = stringResource(Res.string.order_summary_gold_weight),
                        value = "${formatGramsTruncate(quote.goldWeightGrams)} gm",
                    )
                    PriceRow(
                        label = stringResource(Res.string.order_summary_making_charge),
                        value = "₹${formatMoneyCeil(quote.mintingChargeInr)}",
                    )
                    val couponDiscount = (quote.mintingChargeInr - quote.payableChargeInr).coerceAtLeast(0.0)
                    if (couponDiscount > 0) {
                        PriceRow(
                            label = stringResource(Res.string.order_summary_discount),
                            value = "-₹${formatMoney0(couponDiscount)}",
                            valueColor = AppColors.Green600,
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppColors.Divider)
                    PriceRow(
                        label = stringResource(Res.string.order_summary_total),
                        value = "₹${formatMoneyCeil(quote.payableChargeInr)}",
                        bold = true,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SuccessHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Green50),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AppColors.Green600,
                modifier = Modifier.size(56.dp),
            )
            Text(
                text = stringResource(Res.string.order_summary_success_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AppColors.Green700,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(Res.string.order_summary_success_message),
                fontSize = 13.sp,
                color = AppColors.Green600,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SummaryInfoCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(label, fontSize = 13.sp, color = AppColors.Slate600)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.Slate900,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SummarySection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppColors.Slate900)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    bold: Boolean = false,
    valueColor: androidx.compose.ui.graphics.Color = AppColors.Slate900,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) AppColors.Slate900 else AppColors.Slate600,
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) AppColors.Purple700 else valueColor,
        )
    }
}
