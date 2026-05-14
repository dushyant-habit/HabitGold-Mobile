package com.habit.gold.feature.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.AppInfoChip
import com.habit.gold.core.designsystem.HabitGoldDesignSystem
import com.habit.gold.core.localization.appStrings

@Composable
internal fun BrandHero(appName: String) {
    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(HabitGoldDesignSystem.gradients.brandVertical),
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
internal fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
) {
    val strings = appStrings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(HabitGoldDesignSystem.radii.pill)
            .border(1.dp, MaterialTheme.colorScheme.outline, HabitGoldDesignSystem.radii.pill)
            .background(MaterialTheme.colorScheme.surface)
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
                        text = strings.authPhoneNumberPlaceholder,
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
internal fun OtpInputRow(
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
            .background(
                if (isFilled) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
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
internal fun BenefitRow() {
    val strings = appStrings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.xs),
    ) {
        AppInfoChip(
            label = strings.authBenefit24kGold,
            modifier = Modifier.weight(1f),
        )
        AppInfoChip(
            label = strings.authBenefitSecure,
            modifier = Modifier.weight(1f),
        )
        AppInfoChip(
            label = strings.authBenefitInstant,
            modifier = Modifier.weight(1f),
        )
    }
}
