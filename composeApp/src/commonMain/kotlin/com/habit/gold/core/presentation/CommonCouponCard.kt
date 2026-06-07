package com.habit.gold.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import org.jetbrains.compose.resources.stringResource
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*

@Composable
fun CommonCouponCard(
    couponDraft: String,
    appliedCouponCode: String?,
    appliedBenefitText: String?,
    onCouponDraftChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRemoveAppliedCoupon: () -> Unit,
    onShowOffers: () -> Unit,
    isApplyingEnabled: Boolean,
    modifier: Modifier = Modifier,
    availableCoupons: Int = 0,
    onDone: () -> Unit = {},
) {
    val borderColor = AppColors.Slate200
    val primaryTextColor = AppColors.Slate900
    val mutedTextColor = AppColors.Slate500
    val brandColor = AppColors.Primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (appliedCouponCode != null) {
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
                    text = appliedCouponCode,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor,
                )
                appliedBenefitText?.let {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = mutedTextColor,
                    )
                }
            }
            Text(
                text = stringResource(Res.string.trade_buy_change),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = brandColor,
                modifier = Modifier.clickable(onClick = onShowOffers),
            )
            Text(
                text = stringResource(Res.string.trade_buy_remove),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C),
                modifier = Modifier.clickable(onClick = onRemoveAppliedCoupon),
            )
        } else {
            BasicTextField(
                value = couponDraft,
                onValueChange = { onCouponDraftChange(it.uppercase()) },
                singleLine = true,
                cursorBrush = SolidColor(brandColor),
                textStyle = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryTextColor,
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (couponDraft.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.trade_buy_enter_coupon_code),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFC7CAD1),
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Button(
                onClick = onApplyCoupon,
                enabled = isApplyingEnabled && couponDraft.isNotBlank(),
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(999.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandColor,
                    contentColor = Color.White,
                    disabledContainerColor = borderColor,
                    disabledContentColor = mutedTextColor,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_apply),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                modifier = Modifier.clickable(onClick = onShowOffers),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.trade_buy_offers),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = brandColor,
                )
                if (availableCoupons > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = brandColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
