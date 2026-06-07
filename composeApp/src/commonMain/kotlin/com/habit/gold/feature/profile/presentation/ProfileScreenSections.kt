@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.feature.profile.domain.model.ProfileSummary
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_cancel
import habitgoldmobile.composeapp.generated.resources.common_delete
import habitgoldmobile.composeapp.generated.resources.profile_delete_account
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_confirm_body
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_confirm_instruction
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_hub_manage_upi_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_pending
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_retry
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_start
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_verified
import habitgoldmobile.composeapp.generated.resources.profile_kyc_pending
import habitgoldmobile.composeapp.generated.resources.profile_kyc_retry
import habitgoldmobile.composeapp.generated.resources.profile_kyc_start
import habitgoldmobile.composeapp.generated.resources.profile_kyc_verified
import habitgoldmobile.composeapp.generated.resources.profile_loading_error_retry
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileHubShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(30.dp)
                .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(132.dp)
                .height(16.dp)
                .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
        )
        Spacer(modifier = Modifier.height(28.dp))
        repeat(3) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .width(86.dp)
                        .height(12.dp)
                        .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, ProfileCardBorder),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        repeat(if (it == 0) 4 else 2) { rowIndex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .profileShimmerPlaceholder(CircleShape),
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .width(if (rowIndex % 2 == 0) 110.dp else 96.dp)
                                            .height(14.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(if (rowIndex % 2 == 0) 150.dp else 134.dp)
                                            .height(12.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                }
                            }
                            if (rowIndex != (if (it == 0) 3 else 1)) {
                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfilePlaceholderScreen(
    title: String,
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ProfileScreenBackground,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ProfileScreenBackground),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ProfileCardBorder),
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(24.dp),
                    color = ProfileMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}

@Composable
internal fun ProfileSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ProfileSectionLabel,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, ProfileCardBorder),
        ) {
            Column { content() }
        }
    }
}

@Composable
internal fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ProfileNeutralIconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HabitGoldPalette.plum,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ProfilePrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = ProfileMutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD4D4D8),
            )
        }
    }
}

@Composable
internal fun ProfileMenuDivider() {
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
}

@Composable
internal fun KycStatusPill(status: String) {
    val normalizedStatus = status.trim().uppercase()
    val (text, containerColor, textColor) = when (normalizedStatus) {
        "VERIFIED", "ACTIVE" -> Triple(
            stringResource(Res.string.profile_kyc_verified),
            ProfileChipVerified,
            Color(0xFF15803D),
        )
        "PENDING" -> Triple(
            stringResource(Res.string.profile_kyc_pending),
            ProfileChipPending,
            HabitGoldPalette.plum,
        )
        "REJECTED" -> Triple(
            stringResource(Res.string.profile_kyc_retry),
            ProfileChipPending,
            HabitGoldPalette.plum,
        )
        else -> Triple(
            stringResource(Res.string.profile_kyc_start),
            ProfileChipPending,
            HabitGoldPalette.plum,
        )
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

@Composable
internal fun ProfileActionCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    borderColor: Color,
    trailingTint: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ProfilePrimaryText,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = trailingTint,
            )
        }
    }
}

@Composable
internal fun ProfileConfirmationDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isDanger: Boolean,
    showSecondaryButton: Boolean = true,
    emphasizeDismissAction: Boolean = false,
    confirmInFlight: Boolean,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ProfileDangerSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isDanger) Icons.AutoMirrored.Filled.Logout else Icons.Default.Badge,
                        contentDescription = null,
                        tint = ProfileDanger,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfilePrimaryText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = body,
                    fontSize = 14.sp,
                    color = ProfileMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (emphasizeDismissAction && showSecondaryButton) {
                    TextButton(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !confirmInFlight,
                    ) {
                        Text(
                            text = confirmLabel,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    ) {
                        Text(
                            text = stringResource(Res.string.common_cancel),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                } else {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfileDanger),
                        enabled = !confirmInFlight,
                    ) {
                        Text(text = confirmLabel, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (showSecondaryButton) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(Res.string.common_cancel),
                                color = ProfileMutedText,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileDeleteAccountDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ProfileDangerSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = ProfileDanger,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.profile_delete_account),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfilePrimaryText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.profile_delete_account_confirm_body),
                    fontSize = 14.sp,
                    color = ProfileMutedText,
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(Res.string.profile_delete_account_confirm_instruction),
                    fontSize = 14.sp,
                    color = ProfileMutedText,
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.profile_delete_account_placeholder)) },
                    singleLine = true,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProfileDanger,
                        focusedLabelColor = ProfileDanger,
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(Res.string.common_cancel))
                    }
                    TextButton(
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                    ) {
                        Text(
                            text = stringResource(Res.string.common_delete),
                            color = ProfileDanger,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProfileLoadingErrorCard(
    onRefresh: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileCardBorder),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.profile_loading_error_retry),
                color = ProfileMutedText,
                lineHeight = 22.sp,
            )
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
            ) {
                Text(text = stringResource(Res.string.profile_loading_error_retry))
            }
        }
    }
}

@Composable
internal fun manageUpiSubtitle(summary: ProfileSummary?): String {
    val count = summary?.user?.vpas?.size ?: 0
    return if (count > 0) {
        "$count linked UPI ID" + if (count == 1) "" else "s"
    } else {
        stringResource(Res.string.profile_hub_manage_upi_subtitle)
    }
}

@Composable
internal fun kycSubtitle(summary: ProfileSummary?): String {
    return when (summary?.user?.kycStatus?.trim()?.uppercase()) {
        "VERIFIED", "ACTIVE" -> stringResource(Res.string.profile_hub_subtitle_verified)
        "PENDING" -> stringResource(Res.string.profile_hub_subtitle_pending)
        "REJECTED" -> stringResource(Res.string.profile_hub_subtitle_retry)
        else -> stringResource(Res.string.profile_hub_subtitle_start)
    }
}
