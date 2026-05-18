@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import habitgoldmobile.composeapp.generated.resources.common_ok
import habitgoldmobile.composeapp.generated.resources.profile_contact_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_account_section
import habitgoldmobile.composeapp.generated.resources.profile_delete_account
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_blocked_message
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_blocked_title
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_confirm_body
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_confirm_instruction
import habitgoldmobile.composeapp.generated.resources.profile_delete_account_placeholder
import habitgoldmobile.composeapp.generated.resources.profile_delivery_saved_addresses
import habitgoldmobile.composeapp.generated.resources.profile_delivery_saved_addresses_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_delivery_section
import habitgoldmobile.composeapp.generated.resources.profile_delivery_track_order
import habitgoldmobile.composeapp.generated.resources.profile_delivery_track_order_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_help_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_account_details
import habitgoldmobile.composeapp.generated.resources.profile_hub_account_details_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_biometric
import habitgoldmobile.composeapp.generated.resources.profile_hub_biometric_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_contact_us
import habitgoldmobile.composeapp.generated.resources.profile_hub_help_center
import habitgoldmobile.composeapp.generated.resources.profile_hub_kyc
import habitgoldmobile.composeapp.generated.resources.profile_hub_manage_upi
import habitgoldmobile.composeapp.generated.resources.profile_hub_manage_upi_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_nominee
import habitgoldmobile.composeapp.generated.resources.profile_hub_nominee_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_pending
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_retry
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_start
import habitgoldmobile.composeapp.generated.resources.profile_hub_subtitle_verified
import habitgoldmobile.composeapp.generated.resources.profile_hub_title
import habitgoldmobile.composeapp.generated.resources.profile_hub_upi_autopay
import habitgoldmobile.composeapp.generated.resources.profile_hub_upi_autopay_subtitle
import habitgoldmobile.composeapp.generated.resources.profile_kyc_pending
import habitgoldmobile.composeapp.generated.resources.profile_kyc_retry
import habitgoldmobile.composeapp.generated.resources.profile_kyc_start
import habitgoldmobile.composeapp.generated.resources.profile_kyc_verified
import habitgoldmobile.composeapp.generated.resources.profile_loading_error_retry
import habitgoldmobile.composeapp.generated.resources.profile_logout
import habitgoldmobile.composeapp.generated.resources.profile_logout_confirm_body
import habitgoldmobile.composeapp.generated.resources.profile_logout_confirm_cta
import habitgoldmobile.composeapp.generated.resources.profile_logout_confirm_title
import habitgoldmobile.composeapp.generated.resources.profile_phone_fallback
import habitgoldmobile.composeapp.generated.resources.profile_support_section
import habitgoldmobile.composeapp.generated.resources.profile_version_label
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch

data class ProfileBiometricToggleResult(
    val enabled: Boolean,
    val message: String? = null,
)

private val ProfileSectionLabel = Color(0xFF94A3B8)
private val ProfileChipPending = Color(0xFFF3E8FF)
private val ProfileChipVerified = Color(0xFFDCFCE7)
private val ProfileDanger = Color(0xFFDC2626)
private val ProfileDangerSoft = Color(0xFFFEE2E2)

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    appVersion: String,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onDismissError: () -> Unit,
    onOpenPersonalInfo: () -> Unit,
    onOpenNominee: () -> Unit,
    onOpenKyc: () -> Unit,
    biometricEnabled: Boolean,
    biometricSubtitle: String,
    onToggleBiometric: suspend (Boolean) -> ProfileBiometricToggleResult,
    onOpenAutopay: () -> Unit,
    onOpenVpaList: () -> Unit,
    onOpenTrackOrder: () -> Unit,
    onOpenSavedAddresses: () -> Unit,
    onOpenHelpCenter: () -> Unit,
    onOpenContactUs: () -> Unit,
    onConfirmLogout: () -> Unit,
    onConfirmDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteBlockedDialog by remember { mutableStateOf(false) }
    var deleteConfirmationText by remember { mutableStateOf("") }
    var isBiometricBusy by remember { mutableStateOf(false) }

    LaunchedEffect(state.errorMessage) {
        val error = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(error)
        onDismissError()
    }

    val summary = state.summary
    val displayName = summary?.user?.name?.takeIf { it.isNotBlank() } ?: ""
    val displayPhone = summary?.user?.mobileNumber?.takeIf { it.isNotBlank() }
        ?: stringResource(Res.string.profile_phone_fallback)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_hub_title),
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if ((summary == null && state.isLoading) || state.isRefreshing) {
                ProfileHubShimmer()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = displayName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProfilePrimaryText,
                        letterSpacing = (-0.5).sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayPhone,
                        fontSize = 15.sp,
                        color = ProfileMutedText,
                    )
                }
            }

            if (summary == null && !state.isLoading) {
                ProfileLoadingErrorCard(onRefresh = onRefresh)
            } else if (summary != null) {
                Spacer(modifier = Modifier.height(24.dp))
                ProfileSection(
                    title = stringResource(Res.string.profile_account_section),
                    content = {
                        ProfileMenuItem(
                            icon = Icons.Default.PersonOutline,
                            title = stringResource(Res.string.profile_hub_account_details),
                            subtitle = summary.user.email.takeIf { it.isNotBlank() }
                                ?: stringResource(Res.string.profile_hub_account_details_subtitle),
                            onClick = onOpenPersonalInfo,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Autorenew,
                            title = stringResource(Res.string.profile_hub_upi_autopay),
                            subtitle = stringResource(Res.string.profile_hub_upi_autopay_subtitle),
                            onClick = onOpenAutopay,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Payment,
                            title = stringResource(Res.string.profile_hub_manage_upi),
                            subtitle = manageUpiSubtitle(summary),
                            onClick = onOpenVpaList,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Group,
                            title = stringResource(Res.string.profile_hub_nominee),
                            subtitle = summary.user.nominee?.name?.takeIf { it.isNotBlank() }
                                ?.let { "Nominee: $it" }
                                ?: stringResource(Res.string.profile_hub_nominee_subtitle),
                            onClick = onOpenNominee,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Fingerprint,
                            title = stringResource(Res.string.profile_hub_biometric),
                            subtitle = biometricSubtitle,
                            trailingContent = {
                                androidx.compose.material3.Switch(
                                    checked = biometricEnabled,
                                    enabled = !isBiometricBusy,
                                    onCheckedChange = { enabled ->
                                        coroutineScope.launch {
                                            isBiometricBusy = true
                                            val result = onToggleBiometric(enabled)
                                            result.message?.let { snackbarHostState.showSnackbar(it) }
                                            isBiometricBusy = false
                                        }
                                    },
                                    colors = androidx.compose.material3.SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = HabitGoldPalette.plum,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = ProfileCardBorder,
                                        uncheckedBorderColor = Color.Transparent,
                                    ),
                                )
                            },
                            onClick = {
                                coroutineScope.launch {
                                    isBiometricBusy = true
                                    val result = onToggleBiometric(!biometricEnabled)
                                    result.message?.let { snackbarHostState.showSnackbar(it) }
                                    isBiometricBusy = false
                                }
                            },
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Badge,
                            title = stringResource(Res.string.profile_hub_kyc),
                            subtitle = kycSubtitle(summary),
                            trailingContent = {
                                KycStatusPill(status = summary.user.kycStatus)
                            },
                            onClick = onOpenKyc,
                        )
                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileSection(
                    title = stringResource(Res.string.profile_delivery_section),
                    content = {
                        ProfileMenuItem(
                            icon = Icons.Default.LocalShipping,
                            title = stringResource(Res.string.profile_delivery_track_order),
                            subtitle = stringResource(Res.string.profile_delivery_track_order_subtitle),
                            onClick = onOpenTrackOrder,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.HomeWork,
                            title = stringResource(Res.string.profile_delivery_saved_addresses),
                            subtitle = stringResource(Res.string.profile_delivery_saved_addresses_subtitle),
                            onClick = onOpenSavedAddresses,
                        )
                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileSection(
                    title = stringResource(Res.string.profile_support_section),
                    content = {
                        ProfileMenuItem(
                            icon = Icons.Default.HeadsetMic,
                            title = stringResource(Res.string.profile_hub_help_center),
                            subtitle = stringResource(Res.string.profile_help_subtitle),
                            onClick = onOpenHelpCenter,
                        )
                        ProfileMenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Default.Call,
                            title = stringResource(Res.string.profile_hub_contact_us),
                            subtitle = stringResource(Res.string.profile_contact_subtitle),
                            onClick = onOpenContactUs,
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileActionCard(
                title = stringResource(Res.string.profile_logout),
                icon = Icons.AutoMirrored.Filled.Logout,
                backgroundColor = ProfileDangerSoft,
                iconTint = ProfileDanger,
                borderColor = ProfileCardBorder,
                trailingTint = Color(0xFFD4D4D8),
                onClick = { showLogoutDialog = true },
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileActionCard(
                title = stringResource(Res.string.profile_delete_account),
                icon = Icons.Default.DeleteOutline,
                backgroundColor = ProfileDangerSoft,
                iconTint = ProfileDanger,
                borderColor = Color(0xFFFCA5A5),
                trailingTint = ProfileDanger,
                onClick = {
                    deleteConfirmationText = ""
                    if ((summary?.totalGoldBalanceGrams ?: 0.0) > 0.0) {
                        showDeleteBlockedDialog = true
                    } else {
                        showDeleteDialog = true
                    }
                },
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (appVersion.isNotBlank()) {
                Text(
                    text = stringResource(Res.string.profile_version_label, appVersion),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ProfileMutedText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showLogoutDialog) {
        ProfileConfirmationDialog(
            title = stringResource(Res.string.profile_logout_confirm_title),
            body = stringResource(Res.string.profile_logout_confirm_body),
            confirmLabel = stringResource(Res.string.profile_logout_confirm_cta),
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onConfirmLogout()
            },
            isDanger = true,
            emphasizeDismissAction = true,
            confirmInFlight = state.isLogoutInFlight,
        )
    }

    if (showDeleteDialog) {
        ProfileDeleteAccountDialog(
            value = deleteConfirmationText,
            onValueChange = { deleteConfirmationText = it.uppercase() },
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onConfirmDeleteAccount()
            },
            confirmEnabled = deleteConfirmationText == "HABITGOLD" && !state.isDeleteInFlight,
        )
    }

    if (showDeleteBlockedDialog) {
        ProfileConfirmationDialog(
            title = stringResource(Res.string.profile_delete_account_blocked_title),
            body = stringResource(Res.string.profile_delete_account_blocked_message),
            confirmLabel = stringResource(Res.string.common_ok),
            onDismiss = { showDeleteBlockedDialog = false },
            onConfirm = { showDeleteBlockedDialog = false },
            isDanger = true,
            showSecondaryButton = false,
            confirmInFlight = false,
        )
    }
}

@Composable
private fun ProfileHubShimmer() {
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
fun ProfilePlaceholderScreen(
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
private fun ProfileSection(
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
private fun ProfileMenuItem(
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
private fun ProfileMenuDivider() {
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
}

@Composable
private fun KycStatusPill(status: String) {
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
private fun ProfileActionCard(
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
private fun ProfileConfirmationDialog(
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
private fun ProfileDeleteAccountDialog(
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
private fun ProfileLoadingErrorCard(
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
private fun manageUpiSubtitle(summary: ProfileSummary?): String {
    val count = summary?.user?.vpas?.size ?: 0
    return if (count > 0) {
        "$count linked UPI ID" + if (count == 1) "" else "s"
    } else {
        stringResource(Res.string.profile_hub_manage_upi_subtitle)
    }
}

@Composable
private fun kycSubtitle(summary: ProfileSummary?): String {
    return when (summary?.user?.kycStatus?.trim()?.uppercase()) {
        "VERIFIED", "ACTIVE" -> stringResource(Res.string.profile_hub_subtitle_verified)
        "PENDING" -> stringResource(Res.string.profile_hub_subtitle_pending)
        "REJECTED" -> stringResource(Res.string.profile_hub_subtitle_retry)
        else -> stringResource(Res.string.profile_hub_subtitle_start)
    }
}
