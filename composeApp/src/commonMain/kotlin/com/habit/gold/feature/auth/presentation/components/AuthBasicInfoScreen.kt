package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppFieldLabel
import com.habit.gold.core.designsystem.AppOutlinedTextField
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.AppSectionCard
import com.habit.gold.core.designsystem.AppSupportingText
import com.habit.gold.core.designsystem.AppTopBar
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings
import com.habit.gold.feature.auth.presentation.AuthFlowUiState

@Composable
internal fun AuthBasicInfoScreen(
    uiState: AuthFlowUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPinCodeChanged: (String) -> Unit,
    onSubmitBasicInfo: () -> Unit,
) {
    val strings = appStrings
    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = strings.authProfileTitle,
                onBackClick = null,
            )
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(HabitGoldDesignSystem.radii.pill)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
            }
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xl))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.md),
            ) {
                Text(
                    text = strings.authBasicInfoHeading,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = strings.authBasicInfoDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AppSectionCard(highlighted = true) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xxs),
                    ) {
                        Text(
                            text = strings.authVerifiedMobileLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = strings.formatPhoneNumber(uiState.phoneNumber),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                    AppFieldLabel(strings.authLegalNameLabel)
                    AppOutlinedTextField(
                        value = uiState.name,
                        onValueChange = onNameChanged,
                        placeholder = strings.authLegalNamePlaceholder,
                        keyboardType = KeyboardType.Text,
                    )
                    AppSupportingText(strings.authLegalNameSupportingText)
                }
                Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                    AppFieldLabel(strings.authEmailLabel)
                    AppOutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChanged,
                        placeholder = strings.authEmailPlaceholder,
                        keyboardType = KeyboardType.Email,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                    AppFieldLabel(strings.authPinCodeLabel)
                    AppOutlinedTextField(
                        value = uiState.pinCode,
                        onValueChange = onPinCodeChanged,
                        placeholder = strings.authPinCodePlaceholder,
                        keyboardType = KeyboardType.Number,
                    )
                }
                AppErrorText(uiState.errorMessage)
            }
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.md))
            AppPrimaryButton(
                label = strings.authContinueCta,
                isLoading = uiState.isLoading,
                onClick = onSubmitBasicInfo,
            )
        }
    }
}
