package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_proceed
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_buy
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_buy_more
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_coin_weight
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_est_dispatch
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_free_delivery
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_not_enough_gold
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_buy_more_prompt
import habitgoldmobile.composeapp.generated.resources.delivery_catalog_total_gold_available
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun DeliveryCatalogLoadingContent() {
    val transition = rememberInfiniteTransition(label = "catalog_shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            AppColors.Slate200,
            AppColors.Slate100,
            AppColors.Slate200
        ),
        start = Offset(x = -300f + progress * 900f, y = 0f),
        end = Offset(x = progress * 900f, y = 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Gold balance banner placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(shimmerBrush)
        )

        // Shimmer products catalog cards placeholders
        repeat(4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.White),
                border = BorderStroke(1.dp, AppColors.Slate125),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(shimmerBrush)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush)
                        )
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerBrush)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(shimmerBrush)
                    )
                }
            }
        }
    }
}

@Composable
internal fun DeliveryShortfallDialog(
    data: CoinShortfallDialogData,
    onBuyGold: (Double) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = AppColors.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AppColors.Gold50,
                border = BorderStroke(1.dp, AppColors.Amber200),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(Res.string.delivery_catalog_not_enough_gold),
                fontSize = 20.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate900,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.delivery_catalog_buy_more_prompt),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.Slate500,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = AppColors.Slate50,
                border = BorderStroke(1.dp, AppColors.Slate200),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ShortfallMetricRow(
                        label = stringResource(Res.string.delivery_catalog_coin_weight),
                        value = "${formatCatalogGrams(data.requiredGrams)} g",
                    )
                    ShortfallMetricRow(
                        label = stringResource(Res.string.delivery_catalog_total_gold_available),
                        value = "${formatCatalogGrams(data.availableGrams)} g",
                    )
                    HorizontalDivider(color = AppColors.Slate200, thickness = 1.dp)
                    ShortfallMetricRow(
                        label = stringResource(Res.string.delivery_catalog_buy_more),
                        value = "${formatCatalogGrams(data.shortfallGrams)} g",
                        highlight = true,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onBuyGold(data.shortfallGrams) },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = stringResource(Res.string.delivery_catalog_buy),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ShortfallMetricRow(
    label: String,
    value: String,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Slate500,
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) AppColors.Primary else AppColors.Slate900,
        )
    }
}

@Composable
internal fun CoinCatalogItem(
    product: PhysicalCoin,
    cartQuantity: Int,
    onProceedClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        border = BorderStroke(1.dp, AppColors.Slate200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.Slate100)
                    .border(1.dp, AppColors.Slate200, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.productName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        "${product.weightGm}g",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Slate400,
                    )
                }
                if (product.weightGm >= 5.0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        color = AppColors.Green600.copy(alpha = 0.9f),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                        ) {
                            Text(
                                text = stringResource(Res.string.delivery_catalog_free_delivery),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.White,
                                maxLines = 1,
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
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (product.metalStamp.isNotBlank()) {
                    Text(
                        text = product.metalStamp,
                        fontSize = 11.sp,
                        color = AppColors.Slate500,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = formatWeight(product.weightGm),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Slate600,
                )
                product.estimatedDispatchDays?.let { days ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.delivery_catalog_est_dispatch, days.toString()),
                        fontSize = 10.sp,
                        color = AppColors.Slate400,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onProceedClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(Res.string.common_proceed),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.White,
                )
            }
        }
    }
}

internal data class CoinShortfallDialogData(
    val requiredGrams: Double,
    val availableGrams: Double,
    val shortfallGrams: Double,
)

internal fun formatCatalogGrams(value: Double): String {
    val scaled = (value * 10000).let { kotlin.math.round(it).toLong() }
    val whole = scaled / 10000
    val frac = (scaled % 10000).let { if (it < 0) -it else it }
    val base = "${whole}.${frac.toString().padStart(4, '0')}"
    return base.trimEnd('0').trimEnd('.').ifBlank { "0" }
}

private fun formatWeight(weight: Double): String {
    val value = if (weight % 1.0 == 0.0) {
        weight.toInt().toString()
    } else {
        val scaled = (weight * 10).roundToInt()
        val whole = scaled / 10
        val fraction = (scaled % 10).let { if (it < 0) -it else it }
        "$whole.$fraction"
    }
    return "${value}g"
}
