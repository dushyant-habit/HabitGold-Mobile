package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppErrorText
import com.habit.gold.core.designsystem.AppScreen
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
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val keyboardController = LocalSoftwareKeyboardController.current
    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }

    LaunchedEffect(uiState.otpCode) {
        val latestDigits = buildOtpDigits(uiState.otpCode)
        repeat(6) { index ->
            if (otpDigits[index] != latestDigits[index]) {
                otpDigits[index] = latestDigits[index]
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }

    AppScreen {
        Column(modifier = Modifier.fillMaxSize()) {
            AuthOtpHeader(
                title = strings.authOtpTitle,
                onBackClick = onBackToLogin,
            )

            Spacer(modifier = Modifier.height(32.dp))

            androidx.compose.material3.Text(
                text = strings.authOtpHeading,
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Text(
                text = strings.authOtpSentIntro,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            androidx.compose.material3.Text(
                text = strings.authFormattedPhoneNumber(uiState.phoneNumber),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                otpDigits.forEachIndexed { index, value ->
                    AuthOtpDigitField(
                        value = value,
                        onValueChange = { rawValue ->
                            val digits = rawValue.filter(Char::isDigit)
                            when {
                                digits.length > 1 -> {
                                    val normalized = digits.take(6)
                                    repeat(6) { cellIndex ->
                                        otpDigits[cellIndex] = normalized.getOrNull(cellIndex)?.toString().orEmpty()
                                    }
                                    onOtpChanged(normalized)
                                    if (normalized.length == 6) {
                                        keyboardController?.hide()
                                        onVerifyOtp()
                                    } else {
                                        focusRequesters[normalized.length.coerceAtMost(5)].requestFocus()
                                    }
                                }
                                digits.length == 1 -> {
                                    otpDigits[index] = digits
                                    val updatedOtp = otpDigits.joinToString("").take(6)
                                    onOtpChanged(updatedOtp)
                                    if (index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        keyboardController?.hide()
                                    }
                                    if (updatedOtp.length == 6) {
                                        onVerifyOtp()
                                    }
                                }
                                else -> {
                                    otpDigits[index] = ""
                                    onOtpChanged(otpDigits.joinToString("").take(6))
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        isError = uiState.errorMessage != null,
                        imeAction = if (index == 5) ImeAction.Done else ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            },
                            onDone = {
                                keyboardController?.hide()
                                onVerifyOtp()
                            },
                        ),
                        onBackspace = {
                            when {
                                otpDigits[index].isNotEmpty() -> {
                                    otpDigits[index] = ""
                                    onOtpChanged(otpDigits.joinToString("").take(6))
                                    if (index > 0) {
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                    true
                                }
                                index > 0 -> {
                                    otpDigits[index - 1] = ""
                                    onOtpChanged(otpDigits.joinToString("").take(6))
                                    focusRequesters[index - 1].requestFocus()
                                    true
                                }
                                else -> false
                            }
                        },
                        focusRequester = focusRequesters[index],
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AppErrorText(uiState.errorMessage)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (uiState.canResendOtp) {
                    androidx.compose.material3.Text(
                        text = strings.authResendLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(
                            enabled = !uiState.isLoading,
                            onClick = onResendOtp,
                        ),
                    )
                } else {
                    androidx.compose.material3.Text(
                        text = strings.authResendCountdown(uiState.resendSecondsRemaining),
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isLoading) {
                AuthProgressIndicator()
                Spacer(modifier = Modifier.height(HabitGoldDesignSystem.spacing.xs))
            }

            AuthPrimaryButton(
                label = strings.authVerifyAndProceedCta,
                enabled = otpDigits.all { it.isNotEmpty() } && !uiState.isLoading,
                onClick = {
                    keyboardController?.hide()
                    onVerifyOtp()
                },
            )
        }
    }
}
