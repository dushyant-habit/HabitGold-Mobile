package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.habit.gold.core.designsystem.theme.*
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmPaymentDialog(
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
                                contentDescription = stringResource(Res.string.common_close),
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
