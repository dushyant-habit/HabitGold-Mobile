package com.habit.gold.feature.auth.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.localization.appStrings
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.ic_habit_gold_transparent_bg
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun AuthLogoHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.foundation.Image(
            painter = painterResource(Res.drawable.ic_habit_gold_transparent_bg),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
        )
    }
}

@Composable
internal fun AuthPhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isError: Boolean,
) {
    val strings = appStrings
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = HabitGoldPalette.ink,
            letterSpacing = 1.sp,
        ),
        placeholder = {
            Text(
                text = strings.authPhoneNumberPlaceholder,
                color = Color(0xFF9CA3AF),
                fontSize = 16.sp,
            )
        },
        prefix = {
            Row(
                modifier = Modifier.padding(start = 4.dp, end = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "\uD83C\uDDEE\uD83C\uDDF3",
                    fontSize = 20.sp,
                )
                Text(
                    text = "+91",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HabitGoldPalette.ink,
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(HabitGoldPalette.mist),
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = HabitGoldPalette.mist,
            unfocusedBorderColor = HabitGoldPalette.mist,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedTextColor = HabitGoldPalette.ink,
            unfocusedTextColor = HabitGoldPalette.ink,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
internal fun AuthOtpHeader(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = HabitGoldPalette.ink,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = HabitGoldPalette.ink,
        )
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
internal fun AuthBasicDetailsHeader(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = HabitGoldPalette.ink,
                modifier = Modifier.size(32.dp),
            )
        }
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = HabitGoldPalette.ink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-16).dp),
        )
    }
}

@Composable
internal fun AuthFeatureRow() {
    val strings = appStrings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        AuthFeatureItem(
            icon = { Icon(imageVector = Icons.Outlined.WorkspacePremium, contentDescription = null) },
            title = strings.authFeaturePhysical,
        )
        AuthFeatureItem(
            icon = { Icon(imageVector = Icons.Filled.Security, contentDescription = null) },
            title = strings.authFeatureInsured,
        )
        AuthFeatureItem(
            icon = { Icon(imageVector = Icons.Outlined.CurrencyRupee, contentDescription = null) },
            title = strings.authFeatureZeroFees,
        )
    }
}

@Composable
private fun AuthFeatureItem(
    icon: @Composable () -> Unit,
    title: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(110.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E8FF)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.primary,
                ) {
                    icon()
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = HabitGoldPalette.slate,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
        )
    }
}

@Composable
internal fun AuthTermsFooter(
    onOpenUrl: (String) -> Unit,
) {
    val strings = appStrings
    val termsText = buildAnnotatedString {
        append(strings.authTermsFooterPrefix)
        append("\n")
        pushStringAnnotation(tag = "terms", annotation = strings.authTermsUrl)
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
            ),
        ) {
            append(strings.authTermsLabel)
        }
        pop()
        append(" & ")
        pushStringAnnotation(tag = "privacy", annotation = strings.authPrivacyPolicyUrl)
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
            ),
        ) {
            append(strings.authPrivacyPolicyLabel)
        }
        pop()
    }

    ClickableText(
        text = termsText,
        onClick = { offset ->
            termsText.getStringAnnotations(start = offset, end = offset)
                .firstOrNull()
                ?.let { onOpenUrl(it.item) }
        },
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        ),
    )
}

@Composable
internal fun AuthPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = Color(0xFFE5E7EB),
            disabledContentColor = Color(0xFF6B7280),
        ),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

@Composable
internal fun AuthProgressIndicator(
    modifier: Modifier = Modifier,
) {
    val liftPx = with(LocalDensity.current) { 6.dp.toPx() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        val transition = rememberInfiniteTransition(label = "authLoadingDots")
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                val offset by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 360,
                            delayMillis = index * 120,
                            easing = FastOutSlowInEasing,
                        ),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "authLoadingDot$index",
                )
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .graphicsLayer {
                            translationY = -liftPx * offset
                            alpha = 0.45f + (0.55f * offset)
                        }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
internal fun AuthStepProgress() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
internal fun AuthReferralCard(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val strings = appStrings
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp, y = 20.dp)
                    .graphicsLayer { rotationZ = -15f },
            )

            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = strings.authReferralHeading,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HabitGoldPalette.ink,
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(strings.authReferralPlaceholder)
                    },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 8.sp,
                        textAlign = TextAlign.Center,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    singleLine = true,
                )
            }
        }
    }
}

@Composable
internal fun AuthSecurityFooter() {
    val strings = appStrings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = strings.authSecurityMessage,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
internal fun AuthOtpDigitField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Number,
    imeAction: androidx.compose.ui.text.input.ImeAction,
    keyboardActions: KeyboardActions,
    onBackspace: () -> Boolean,
    focusRequester: FocusRequester,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(64.dp)
            .focusRequester(focusRequester)
            .onPreviewKeyEvent { event ->
                event.type == KeyEventType.KeyDown &&
                    event.key == Key.Backspace &&
                    onBackspace()
            },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = HabitGoldPalette.mist,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedTextColor = HabitGoldPalette.ink,
            unfocusedTextColor = HabitGoldPalette.ink,
            errorTextColor = HabitGoldPalette.ink,
        ),
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
    )
}

internal fun buildOtpDigits(otp: String): List<String> {
    return List(6) { index -> otp.getOrNull(index)?.toString().orEmpty() }
}
