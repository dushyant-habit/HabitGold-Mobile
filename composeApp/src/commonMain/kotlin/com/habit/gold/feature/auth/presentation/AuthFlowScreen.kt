package com.habit.gold.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppFieldLabel
import com.habit.gold.core.designsystem.AppFooterText
import com.habit.gold.core.designsystem.AppInfoChip
import com.habit.gold.core.designsystem.AppOutlinedTextField
import com.habit.gold.core.designsystem.AppPrimaryButton
import com.habit.gold.core.designsystem.AppScreen
import com.habit.gold.core.designsystem.AppSectionCard
import com.habit.gold.core.designsystem.AppSupportingText
import com.habit.gold.core.designsystem.AppTopBar
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.designsystem.HabitGoldPalette

@Composable
fun AuthFlowScreen(
    uiState: AuthFlowUiState,
    onPhoneChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
    onOtpChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onBackToLogin: () -> Unit,
    onResendOtp: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPinCodeChanged: (String) -> Unit,
    onSubmitBasicInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when (uiState.screen) {
            AuthStep.Login -> LoginScreen(
                uiState = uiState,
                onPhoneChanged = onPhoneChanged,
                onRequestOtp = onRequestOtp,
            )
            AuthStep.Otp -> OtpScreen(
                uiState = uiState,
                onOtpChanged = onOtpChanged,
                onVerifyOtp = onVerifyOtp,
                onBackToLogin = onBackToLogin,
                onResendOtp = onResendOtp,
            )
            AuthStep.BasicInfo -> BasicInfoScreen(
                uiState = uiState,
                onNameChanged = onNameChanged,
                onEmailChanged = onEmailChanged,
                onPinCodeChanged = onPinCodeChanged,
                onSubmitBasicInfo = onSubmitBasicInfo,
            )
        }
    }
}

@Composable
private fun LoginScreen(
    uiState: AuthFlowUiState,
    onPhoneChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
) {
    AppScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.sm))
            BrandHero(appName = uiState.appName)
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.sm))
            Text(
                text = "Invest in a habit. Invest in gold.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.sm),
            ) {
                AppFieldLabel("MOBILE NUMBER")
                PhoneNumberField(
                    value = uiState.phoneNumber,
                    onValueChange = onPhoneChanged,
                    enabled = !uiState.isLoading,
                )
                AppErrorText(uiState.errorMessage)
                AppPrimaryButton(
                    label = "Get OTP",
                    isLoading = uiState.isLoading,
                    onClick = onRequestOtp,
                )
            }
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xl))
            BenefitRow()
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.md))
            AppFooterText(
                "By continuing, you agree to our Terms & Conditions and Privacy Policy.",
            )
        }
    }
}

@Composable
private fun OtpScreen(
    uiState: AuthFlowUiState,
    onOtpChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onBackToLogin: () -> Unit,
    onResendOtp: () -> Unit,
) {
    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Enter OTP",
            onBackClick = onBackToLogin,
        )
        Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xxxl))
        Text(
            text = "Enter your OTP",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
        Text(
            text = "We’ve sent a 6-digit code to +91 ${uiState.phoneNumber}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (uiState.otpRefId.isNotBlank()) {
            Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
            AppSectionCard(highlighted = true) {
                Text(
                    text = "Reference ID: ${uiState.otpRefId}",
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
            label = "Verify OTP",
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
                    "Didn’t receive the OTP?"
                } else {
                    "Resend available in ${uiState.resendSecondsRemaining}s"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(HabitGoldDesignSystem.spacing.xxs + 2.dp))
            Text(
                text = "Resend",
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

@Composable
private fun BasicInfoScreen(
    uiState: AuthFlowUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPinCodeChanged: (String) -> Unit,
    onSubmitBasicInfo: () -> Unit,
) {
    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = "Profile",
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
                text = "Tell us about yourself",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "We need these details to set up your HabitGold profile across Android and iOS.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppSectionCard(highlighted = true) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xxs),
                ) {
                    Text(
                        text = "Verified mobile number",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "+91 ${uiState.phoneNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                AppFieldLabel("LEGAL NAME")
                AppOutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    placeholder = "e.g. Dushyant Mainwal",
                    keyboardType = KeyboardType.Text,
                )
                AppSupportingText("Must match your bank account records exactly.")
            }
            Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                AppFieldLabel("EMAIL ADDRESS")
                AppOutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChanged,
                    placeholder = "you@example.com",
                    keyboardType = KeyboardType.Email,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs)) {
                AppFieldLabel("PINCODE")
                AppOutlinedTextField(
                    value = uiState.pinCode,
                    onValueChange = onPinCodeChanged,
                    placeholder = "Enter 6-digit pincode",
                    keyboardType = KeyboardType.Number,
                )
            }
            AppErrorText(uiState.errorMessage)
        }
        Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.md))
        AppPrimaryButton(
            label = "Continue",
            isLoading = uiState.isLoading,
            onClick = onSubmitBasicInfo,
        )
    }
    }
}

@Composable
private fun BrandHero(appName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    HabitGoldDesignSystem.gradients.brandVertical
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "HG",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                ),
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = appName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(HabitGoldDesignSystem.radii.pill)
            .border(1.dp, MaterialTheme.colorScheme.outline, HabitGoldDesignSystem.radii.pill)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "🇮🇳",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "+91",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(26.dp)
                .background(MaterialTheme.colorScheme.outline),
        )
        Spacer(modifier = Modifier.width(14.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.5.sp,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isBlank()) {
                    Text(
                        text = "98765 43210",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF9CA3AF),
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun OtpInputRow(
    value: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle.Default,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(6) { index ->
                        val digit = value.getOrNull(index)?.toString().orEmpty()
                        OtpDigitBox(
                            digit = digit,
                            isFilled = digit.isNotEmpty(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.02f),
                ) {
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFilled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(HabitGoldDesignSystem.radii.md)
            .background(if (isFilled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.5.dp,
                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = HabitGoldDesignSystem.radii.md,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = digit,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BenefitRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs),
    ) {
        AppInfoChip(
            label = "24K Gold",
            modifier = Modifier.weight(1f),
        )
        AppInfoChip(
            label = "Secure",
            modifier = Modifier.weight(1f),
        )
        AppInfoChip(
            label = "Instant",
            modifier = Modifier.weight(1f),
        )
    }
}
