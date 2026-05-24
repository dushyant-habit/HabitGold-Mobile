package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppFieldLabel
import com.habit.gold.core.designsystem.AppOutlinedTextField
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.localization.appStrings
import com.habit.gold.feature.auth.domain.AuthValidators
import com.habit.gold.feature.auth.presentation.AuthFlowUiState

@Composable
internal fun AuthBasicDetailsScreen(
    uiState: AuthFlowUiState,
    onBackToOtp: () -> Unit,
    onLegalNameChanged: (String) -> Unit,
    onReferralCodeChanged: (String) -> Unit,
    onPinCodeChanged: (String) -> Unit,
    onSubmitBasicDetails: () -> Unit,
) {
    val strings = appStrings
    val canSubmit = AuthValidators.isLegalNameValid(uiState.legalName) &&
        (!uiState.isPinCodeRequired || AuthValidators.isPinCodeValid(uiState.pinCode)) &&
        !uiState.isLoading

    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
            AuthBasicDetailsHeader(
                title = strings.authProfileTitle,
                onBackClick = onBackToOtp,
            )

            Spacer(modifier = Modifier.height(8.dp))
            AuthStepProgress()
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = strings.authBasicDetailsHeading,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = HabitGoldPalette.ink,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.authBasicDetailsDescription,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppFieldLabel(strings.authLegalNameLabel)
                Spacer(modifier = Modifier.height(8.dp))
                AppOutlinedTextField(
                    value = uiState.legalName,
                    onValueChange = onLegalNameChanged,
                    placeholder = strings.authLegalNamePlaceholder,
                    keyboardType = KeyboardType.Text,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.authLegalNameSupportingText,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (uiState.isPinCodeRequired) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AppFieldLabel(strings.authPinCodeLabel)
                    Spacer(modifier = Modifier.height(8.dp))
                    AppOutlinedTextField(
                        value = uiState.pinCode,
                        onValueChange = onPinCodeChanged,
                        placeholder = strings.authPinCodePlaceholder,
                        keyboardType = KeyboardType.Number,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                AuthReferralCard(
                    value = uiState.referralCode,
                    onValueChange = onReferralCodeChanged,
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppErrorText(uiState.errorMessage)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            AuthSecurityFooter()
            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Button(
                onClick = onSubmitBasicDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(bottom = 16.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                enabled = canSubmit,
            ) {
                if (uiState.isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.height(24.dp),
                    )
                } else {
                    Text(
                        text = strings.authConfirmAndProceedCta,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
