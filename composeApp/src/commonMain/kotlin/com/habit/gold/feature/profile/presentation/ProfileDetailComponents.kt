@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.composed
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.presentation.clearFocusOnTapOutside
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.common_ok
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

internal val ProfileScreenBackground = Color(0xFFF8F8FB)
internal val ProfileCardBorder = Color(0xFFE5E7EB)
internal val ProfilePrimaryText = Color(0xFF0F172A)
internal val ProfileMutedText = Color(0xFF64748B)
internal val ProfileFieldLabel = Color(0xFF475569)
internal val ProfileFieldPlaceholder = Color(0xFF94A3B8)
internal val ProfileNeutralIconBackground = Color(0xFFF6F1FB)
internal val ProfileError = Color(0xFFDC2626)
internal val ProfileShimmerBase = Color(0xFFE8ECF3)
internal val ProfileShimmerHighlight = Color(0xFFF6F8FB)

@Composable
internal fun ProfileDetailScaffold(
    title: String,
    onBackClick: () -> Unit,
    bottomButtonLabel: String,
    bottomButtonEnabled: Boolean,
    bottomButtonLoading: Boolean,
    onBottomButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = HabitGoldPalette.plum,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            ) {
                Button(
                    onClick = onBottomButtonClick,
                    enabled = bottomButtonEnabled && !bottomButtonLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
                ) {
                    if (bottomButtonLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = bottomButtonLabel,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clearFocusOnTapOutside { focusManager.clearFocus(force = true) }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            content = content,
        )
    }
}

@Composable
internal fun ProfileFieldBlock(
    label: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            Text(
                text = label,
                fontSize = 14.sp,
                color = ProfileFieldLabel,
                fontWeight = FontWeight.Medium,
            )
            content()
        },
    )
}

@Composable
internal fun ProfileEditorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = ImeAction.Next,
    trailingIcon: (@Composable (() -> Unit))? = null,
    leadingIcon: (@Composable (() -> Unit))? = null,
    supportingText: String? = null,
    errorText: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            readOnly = readOnly,
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = keyboardCapitalization,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            placeholder = {
                Text(
                    text = placeholder,
                    color = ProfileFieldPlaceholder,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HabitGoldPalette.plum,
                unfocusedBorderColor = ProfileCardBorder,
                disabledBorderColor = ProfileCardBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedTextColor = ProfilePrimaryText,
                unfocusedTextColor = ProfilePrimaryText,
                disabledTextColor = ProfilePrimaryText,
                focusedPlaceholderColor = ProfileFieldPlaceholder,
                unfocusedPlaceholderColor = ProfileFieldPlaceholder,
                cursorColor = HabitGoldPalette.plum,
                focusedTrailingIconColor = HabitGoldPalette.plum,
                unfocusedTrailingIconColor = ProfileMutedText,
                focusedLeadingIconColor = ProfileMutedText,
                unfocusedLeadingIconColor = ProfileMutedText,
            ),
        )
        when {
            errorText != null -> Text(
                text = errorText,
                fontSize = 12.sp,
                color = ProfileError,
            )
            supportingText != null -> Text(
                text = supportingText,
                fontSize = 12.sp,
                color = ProfileMutedText,
            )
        }
    }
}

internal fun Modifier.profileShimmerPlaceholder(
    shape: Shape,
    baseColor: Color = ProfileShimmerBase,
    highlightColor: Color = ProfileShimmerHighlight,
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "profileShimmer")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "profileShimmerProgress",
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(800f * progress, 240f * progress),
    )
    clip(shape)
        .background(shimmerBrush)
        .alpha(0.98f)
}

@Composable
internal fun ProfileSelectionField(
    value: String,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        ProfileEditorTextField(
            value = value,
            onValueChange = {},
            placeholder = placeholder,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            },
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick),
        )
    }
}

@Composable
internal fun ProfileSelectionSheet(
    title: String,
    options: List<String>,
    selectedValue: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ProfilePrimaryText,
                modifier = Modifier.padding(vertical = 12.dp),
            )
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(option)
                            onDismiss()
                        }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = ProfilePrimaryText,
                        modifier = Modifier.weight(1f),
                    )
                    if (option == selectedValue) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(HabitGoldPalette.plum, CircleShape),
                        )
                    }
                }
                if (index != options.lastIndex) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
internal fun ProfileErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfilePrimaryText,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = ProfileMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(Res.string.common_cancel),
                            color = HabitGoldPalette.plum,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

internal fun filterLegalNameInput(input: String): String {
    return input.filter { it.isLetter() || it.isWhitespace() || it == '.' }
        .replace(Regex("\\s+"), " ")
        .take(60)
}

internal fun sanitizeNomineeNameInput(input: String): String {
    return input.filter { it.isLetter() || it.isWhitespace() || it == '.' }
        .replace(Regex("\\s+"), " ")
        .take(60)
}

internal fun formatDateOfBirthForDisplay(raw: String?): String {
    val value = raw?.trim().orEmpty()
    if (value.isBlank()) return ""
    if (Regex("""\d{2}/\d{2}/\d{4}""").matches(value)) return value
    if (Regex("""\d{4}-\d{2}-\d{2}""").matches(value)) {
        val parts = value.split("-")
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }
    val isoDateCandidate = value.take(10)
    return if (Regex("""\d{4}-\d{2}-\d{2}""").matches(isoDateCandidate)) {
        val parts = isoDateCandidate.split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } else {
        value
    }
}

internal fun toIsoDateOrNull(raw: String): String? {
    val value = raw.trim()
    if (value.isBlank()) return null
    if (Regex("""\d{4}-\d{2}-\d{2}""").matches(value)) return value
    val parts = value.split("/")
    if (parts.size == 3 && parts[2].length == 4) {
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2]
        return "$year-$month-$day"
    }
    return null
}

internal fun String.normalizedProfileGender(): String {
    return trim().uppercase().replaceFirstChar { it.titlecase() }
}

@Composable
internal fun ProfileDatePickerDialog(
    selectedDate: String?,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit,
) {
    val initialMillis = remember(selectedDate) {
        selectedDate
            ?.let(::toIsoDateOrNull)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?.atStartOfDayIn(TimeZone.UTC)
            ?.toEpochMilliseconds()
    }
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selected = Instant.fromEpochMilliseconds(selectedMillis)
                            .toLocalDateTime(TimeZone.UTC)
                            .date
                        onDateSelected(
                            selected.day.toString().padStart(2, '0') +
                                "/" +
                                (selected.month.ordinal + 1).toString().padStart(2, '0') +
                                "/" +
                                selected.year,
                        )
                    }
                    onDismiss()
                },
            ) {
                Text(
                    text = stringResource(Res.string.common_ok),
                    color = HabitGoldPalette.plum,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.common_cancel),
                    color = ProfileMutedText,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = HabitGoldPalette.plum,
                selectedYearContainerColor = HabitGoldPalette.plum,
                todayDateBorderColor = HabitGoldPalette.plum,
            ),
        )
    }
}
