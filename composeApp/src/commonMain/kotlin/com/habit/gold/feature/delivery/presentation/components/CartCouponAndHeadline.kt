package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import com.habit.gold.feature.trade.domain.model.TradeCouponType
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CouponCodeCard(
    couponCode: String,
    appliedCoupon: String?,
    couponType: TradeCouponType? = null,
    availableCouponsCount: Int = 0,
    onShowOffers: () -> Unit = {},
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    if (appliedCoupon != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.White)
                .border(1.dp, AppColors.Slate125, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Text(
                    text = appliedCoupon,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Slate950,
                    maxLines = 1,
                )
                if (couponType == TradeCouponType.FREE_DELIVERY) {
                    Text(
                        text = "Free Delivery applied",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Green700,
                        maxLines = 2,
                    )
                } else {
                    Text(
                        text = "Discount coupon applied",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Slate500,
                        maxLines = 2,
                    )
                }
            }
            Text(
                text = "Change",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                modifier = Modifier.clickable(onClick = onShowOffers),
            )
            Text(
                text = stringResource(Res.string.delivery_cart_remove),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Red600,
                modifier = Modifier.clickable(onClick = onRemove),
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.White)
                .border(1.dp, AppColors.Slate125, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BasicTextField(
                value = couponCode,
                onValueChange = { onCodeChange(it.uppercase()) },
                singleLine = true,
                cursorBrush = SolidColor(AppColors.Primary),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Slate950,
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(AppColors.White, RoundedCornerShape(10.dp))
                    .border(1.dp, AppColors.Slate200, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (couponCode.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.delivery_cart_enter_coupon_code),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Slate300,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Button(
                onClick = onApply,
                enabled = couponCode.isNotBlank(),
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(999.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    contentColor = AppColors.White,
                    disabledContainerColor = AppColors.Slate200,
                    disabledContentColor = AppColors.Slate400,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.delivery_cart_apply),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                modifier = Modifier.clickable(onClick = onShowOffers),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Offers",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
fun CoinHeadlineCard(
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
