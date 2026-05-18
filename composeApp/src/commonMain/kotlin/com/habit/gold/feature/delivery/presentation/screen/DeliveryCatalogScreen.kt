package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.core.localization.AppStrings
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogState
import com.habit.gold.feature.delivery.presentation.DeliveryIntent
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryCatalogScreen(
    state: DeliveryCatalogState,
    onIntent: (DeliveryIntent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToBuyGold: (String) -> Unit
) {
    var shortfallDialog by remember { mutableStateOf<CoinShortfallDialogData?>(null) }

    LaunchedEffect(Unit) {
        onIntent(DeliveryIntent.RefreshGoldBalance)
        if (state.coins.isEmpty()) {
            onIntent(DeliveryIntent.LoadProducts)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.delivery_catalog_choose_coin),
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Black,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.Black)
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoadingProducts -> {
                    DeliveryCatalogLoadingContent()
                }
                state.coins.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.delivery_catalog_no_products),
                            fontSize = 14.sp,
                            color = AppColors.Slate500,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = AppColors.Gold50,
                                border = BorderStroke(1.dp, AppColors.Yellow400)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(Res.string.delivery_catalog_total_gold_available),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.Amber800
                                    )
                                    Text(
                                        text = "${formatCatalogGrams(state.totalGoldBalanceGrams)} g",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.Amber900
                                    )
                                }
                            }
                        }
                        items(state.coins, key = { it.id }) { coin ->
                            CoinCatalogItem(
                                product = coin,
                                cartQuantity = state.cartItems[coin.id] ?: 0,
                                onProceedClick = {
                                    val availableGrams = state.totalGoldBalanceGrams
                                    val requiredGrams = coin.weightGm
                                    val shortfallGrams = (requiredGrams - availableGrams).coerceAtLeast(0.0)
                                    if (shortfallGrams > 0.0) {
                                        shortfallDialog = CoinShortfallDialogData(
                                            requiredGrams = requiredGrams,
                                            availableGrams = availableGrams,
                                            shortfallGrams = shortfallGrams
                                        )
                                    } else {
                                        // Update quantity and proceed to cart
                                        onIntent(DeliveryIntent.UpdateQuantity(coin.id, 1))
                                        onNavigateToCart()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    shortfallDialog?.let { dialogData ->
        Dialog(
            onDismissRequest = { shortfallDialog = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = AppColors.White,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = AppColors.Gold50,
                        border = BorderStroke(1.dp, AppColors.Amber200)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.delivery_catalog_not_enough_gold),
                        fontSize = 20.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.delivery_catalog_buy_more_prompt),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = AppColors.Slate50,
                        border = BorderStroke(1.dp, AppColors.Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ShortfallMetricRow(
                                label = stringResource(Res.string.delivery_catalog_coin_weight),
                                value = "${formatCatalogGrams(dialogData.requiredGrams)} g"
                            )
                            ShortfallMetricRow(
                                label = stringResource(Res.string.delivery_catalog_redeemable_gold),
                                value = "${formatCatalogGrams(dialogData.availableGrams)} g"
                            )
                            HorizontalDivider(color = AppColors.Slate200, thickness = 1.dp)
                            ShortfallMetricRow(
                                label = stringResource(Res.string.delivery_catalog_buy_more),
                                value = "${formatCatalogGrams(dialogData.shortfallGrams)} g",
                                highlight = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val gramsToBuy = formatShortfallForBuy(dialogData.shortfallGrams)
                            shortfallDialog = null
                            onNavigateToBuyGold(gramsToBuy)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.delivery_catalog_buy),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCatalogLoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = AppColors.Primary)
    }
}

@Composable
private fun ShortfallMetricRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Slate500
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) AppColors.Primary else AppColors.Slate900
        )
    }
}

@Composable
fun CoinCatalogItem(
    product: PhysicalCoin,
    cartQuantity: Int,
    onProceedClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        border = BorderStroke(1.dp, AppColors.Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Slate100)
                    .border(1.dp, AppColors.Slate200, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.productName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        "${product.weightGm}g",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate400
                    )
                }
                if (product.weightGm >= 5.0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(18.dp),
                        color = AppColors.Green600.copy(alpha = 0.9f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "FREE DELIVERY",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate950,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (product.metalStamp.isNotBlank()) {
                    Text(
                        text = product.metalStamp,
                        fontSize = 11.sp,
                        color = AppColors.Slate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatWeight(product.weightGm)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Slate600
                    )
                }
                product.estimatedDispatchDays?.let { days ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.delivery_catalog_est_dispatch, days.toString()),
                        fontSize = 10.sp,
                        color = AppColors.Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onProceedClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Proceed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.White
                )
            }
        }
    }
}

private fun formatWeight(w: Double): String {
    val s = if (w % 1.0 == 0.0) {
        w.toInt().toString()
    } else {
        val scaled = (w * 10).roundToInt()
        val whole = scaled / 10
        val frac = (scaled % 10).let { if (it < 0) -it else it }
        "$whole.$frac"
    }
    return "${s}g"
}

private data class CoinShortfallDialogData(
    val requiredGrams: Double,
    val availableGrams: Double,
    val shortfallGrams: Double
)

private fun formatCatalogGrams(value: Double): String {
    val scaled = (value * 10000).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 10000
    val frac = (scaled % 10000).let { if (it < 0) -it else it }
    val base = "${whole}.${frac.toString().padStart(4, '0')}"
    return base.trimEnd('0').trimEnd('.').ifBlank { "0" }
}

private fun formatShortfallForBuy(value: Double): String {
    val scaled = (value * 10000).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 10000
    val frac = (scaled % 10000).let { if (it < 0) -it else it }
    val base = "${whole}.${frac.toString().padStart(4, '0')}"
    return base.trimEnd('0').trimEnd('.').ifBlank { "0" }
}
