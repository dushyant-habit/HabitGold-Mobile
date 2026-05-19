package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.presentation.DeliveryCatalogState
import com.habit.gold.feature.delivery.presentation.DeliveryIntent
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryCatalogScreen(
    state: DeliveryCatalogState,
    onIntent: (DeliveryIntent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToBuyGold: (Double) -> Unit
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                                        text = stringResource(Res.string.delivery_catalog_redeemable_gold),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.Amber800
                                    )
                                    Text(
                                        text = "${formatCatalogGrams(state.redeemableGoldGrams)} g",
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
                                    val availableGrams = state.redeemableGoldGrams
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
            DeliveryShortfallDialog(
                data = dialogData,
                onBuyGold = { gramsToBuy ->
                    shortfallDialog = null
                    onNavigateToBuyGold(gramsToBuy)
                },
            )
        }
    }
}
