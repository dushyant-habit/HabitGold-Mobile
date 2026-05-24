package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppFieldLabel
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings
import com.habit.gold.feature.auth.domain.AuthValidators
import com.habit.gold.feature.auth.presentation.AuthFlowUiState

@Composable
internal fun AuthLoginScreen(
    uiState: AuthFlowUiState,
    onPhoneChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
) {
    val strings = appStrings
    val uriHandler = LocalUriHandler.current
    val inlinePhoneError = uiState.errorMessage

    AppScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AuthLogoHero()
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = strings.authTagline,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AppFieldLabel(strings.authMobileNumberLabel)
                Spacer(modifier = Modifier.height(8.dp))
                AuthPhoneNumberField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChanged,
                    enabled = !uiState.isLoading,
                    isError = inlinePhoneError != null,
                )
                Spacer(modifier = Modifier.height(4.dp))
                AppErrorText(inlinePhoneError)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                AuthProgressIndicator(
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            AuthPrimaryButton(
                label = strings.authRequestOtpCta,
                enabled = !uiState.isLoading && AuthValidators.isPhoneValid(uiState.phoneNumber),
                onClick = onRequestOtp,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.height(24.dp),
                    )
                },
            )

            Spacer(modifier = Modifier.weight(1f))
            AuthFeatureRow()
            Spacer(modifier = Modifier.height(32.dp))
            AuthTermsFooter(onOpenUrl = uriHandler::openUri)
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
        }
    }
}
