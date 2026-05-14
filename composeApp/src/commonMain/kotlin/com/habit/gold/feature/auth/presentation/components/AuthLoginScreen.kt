package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppFieldLabel
import com.habit.gold.core.designsystem.AppFooterText
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings
import com.habit.gold.feature.auth.presentation.AuthFlowUiState

@Composable
internal fun AuthLoginScreen(
    uiState: AuthFlowUiState,
    onPhoneChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
) {
    val strings = appStrings
    AppScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.sm))
            BrandHero(appName = uiState.appName)
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.sm))
            Text(
                text = strings.authTagline,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm),
            ) {
                AppFieldLabel(strings.authMobileNumberLabel)
                PhoneNumberField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChanged,
                    enabled = !uiState.isLoading,
                )
                AppErrorText(uiState.errorMessage)
                AppPrimaryButton(
                    label = strings.authRequestOtpCta,
                    isLoading = uiState.isLoading,
                    onClick = onRequestOtp,
                )
            }
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xl))
            BenefitRow()
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.md))
            AppFooterText(strings.authTermsFooter)
        }
    }
}
