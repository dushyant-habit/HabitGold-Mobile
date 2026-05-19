package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.*
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PaymentDetailsCard(
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
fun SecureOrderNote() {
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
