package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CouponCodeCard(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Green50, RoundedCornerShape(12.dp))
                        .border(1.dp, AppColors.Green200, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AppColors.Green600,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = appliedCoupon, fontWeight = FontWeight.Bold, color = AppColors.Green800)
                    }
                    TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
                        Text(
                            text = stringResource(Res.string.delivery_cart_remove),
                            color = AppColors.Red600,
                            fontWeight = FontWeight.SemiBold
                        )
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
                        placeholder = { Text(stringResource(Res.string.delivery_cart_enter_coupon_code)) },
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
                        modifier = Modifier.height(56.dp),
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
