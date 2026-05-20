package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.trade.domain.model.TradeAvailableCoupon

@Composable
fun DeliveryCouponSheet(
    coupons: List<TradeAvailableCoupon>,
    appliedCouponCode: String?,
    onApplyCoupon: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = "Available Coupons",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.Slate950
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Choose a coupon to apply to your delivery order",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Slate500
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (coupons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No available coupons found for delivery",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.Slate500
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(coupons) { coupon ->
                    val isSelected = appliedCouponCode?.equals(coupon.code, ignoreCase = true) == true
                    
                    val subtitle = coupon.description?.takeIf { it.isNotBlank() }
                        ?: "Save ₹${coupon.estimatedSaving} on delivery charges"
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AppColors.Primary.copy(alpha = 0.08f) else AppColors.White)
                            .border(
                                1.dp,
                                if (isSelected) AppColors.Primary.copy(alpha = 0.25f) else AppColors.Slate125,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onApplyCoupon(coupon.code) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = coupon.code,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Slate950,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Slate500,
                                maxLines = 2
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(AppColors.Primary)
                                    .padding(horizontal = 14.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Apply",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppColors.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
