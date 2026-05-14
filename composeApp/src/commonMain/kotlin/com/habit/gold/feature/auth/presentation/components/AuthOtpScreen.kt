package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.AppSectionCard
import com.habit.gold.core.designsystem.AppTopBar
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings
import com.habit.gold.feature.auth.presentation.AuthFlowUiState

@Composable
internal fun AuthOtpScreen(
    uiState: AuthFlowUiState,
    onOtpChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onBackToLogin: () -> Unit,
    onResendOtp: () -> Unit,
) {
    val strings = appStrings
    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = strings.authOtpTitle,
                onBackClick = onBackToLogin,
            )
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xxxl))
            Text(
                text = strings.authOtpHeading,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
            Text(
                text = strings.authOtpSentMessage(uiState.phoneNumber),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (uiState.otpRefId.isNotBlank()) {
                Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
                AppSectionCard(highlighted = true) {
                    Text(
                        text = strings.authOtpReferenceId(uiState.otpRefId),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xl))
            OtpInputRow(
                value = uiState.otpCode,
                onValueChange = onOtpChanged,
            )
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.sm))
            AppErrorText(uiState.errorMessage)
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.lg))
            AppPrimaryButton(
                label = strings.authVerifyOtpCta,
                isLoading = uiState.isLoading,
                onClick = onVerifyOtp,
            )
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.lg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (uiState.canResendOtp) {
                        strings.authDidNotReceiveOtp
                    } else {
                        strings.authResendCountdown(uiState.resendSecondsRemaining)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(HabitGoldDesignSystem.spacing.xxs + 2.dp))
                Text(
                    text = strings.authResendLabel,
                    color = if (uiState.canResendOtp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.clickable(
                        enabled = uiState.canResendOtp,
                        onClick = onResendOtp,
                    ),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
