package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.delivery_address_otp
import habitgoldmobile.composeapp.generated.resources.delivery_address_otp_message
import habitgoldmobile.composeapp.generated.resources.delivery_address_otp_title
import habitgoldmobile.composeapp.generated.resources.delivery_address_resend_otp
import habitgoldmobile.composeapp.generated.resources.delivery_address_verify
import org.jetbrains.compose.resources.stringResource

@Composable
fun OtpVerificationDialog(
    address: SavedAddress,
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    isVerifying: Boolean,
    errorMessage: String? = null
) {
    var otp by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = stringResource(Res.string.delivery_address_otp_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.Slate950,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.delivery_address_otp_message),
                    fontSize = 14.sp,
                    color = AppColors.Slate600,
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6) otp = it.filter(Char::isDigit) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.delivery_address_otp)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (errorMessage != null) AppColors.Red600 else AppColors.Purple700,
                        unfocusedBorderColor = if (errorMessage != null) AppColors.Red600 else AppColors.Slate300,
                        focusedLabelColor = AppColors.Purple700,
                    ),
                    isError = errorMessage != null
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = AppColors.Red600,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                TextButton(
                    onClick = onResend,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(
                        stringResource(Res.string.delivery_address_resend_otp),
                        color = AppColors.Purple700,
                        fontSize = 13.sp
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.common_cancel), color = AppColors.Slate500)
            }
        },
        confirmButton = {
            Button(
                onClick = { onVerify(otp) },
                enabled = otp.length == 6 && !isVerifying,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Purple700,
                    disabledContainerColor = AppColors.Purple200,
                ),
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppColors.White,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(stringResource(Res.string.delivery_address_verify))
            }
        },
    )
}
