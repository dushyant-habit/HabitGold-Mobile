package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun RewardsReferDetailScreen(
    state: RewardsReferDetailState,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    onStartSipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val shareLauncher = rememberRewardsShareLauncher()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showQrDialog by rememberSaveable { mutableStateOf(false) }
    val inviteMessage = remember(state.ui.referralCode) { referralInviteMessage(state.ui.referralCode) }
    val shareInvite: () -> Unit = {
        shareLauncher.launch(inviteMessage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Neutral05,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding(),
            ) {
                RewardsCenterTitleTopBar(
                    title = "Refer & Earn",
                    onBackClick = onBackClick,
                    trailing = {
                        IconButton(onClick = onHistoryClick) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Rewards history",
                                tint = Slate800,
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .navigationBarsPadding(),
            ) {
                HorizontalDivider(color = Slate100, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = shareInvite,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple700),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Invite", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = { showQrDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Slate800,
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Slate500,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("My QR", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        },
    ) { paddingValues ->
        if (state.isLoading) {
            RewardsReferDetailLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 12.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    RewardsDetailTopWinCard(
                        lifetimeEarningsDisplay = state.ui.lifetimeEarningsDisplay,
                        activeFriendsCount = state.ui.activeFriendsCount,
                        onShareClick = shareInvite,
                    )
                }
                item {
                    RewardsBoosterCard(
                        ui = state.ui,
                        onBuyNowClick = onBuyNowClick,
                        onInviteClick = shareInvite,
                        onStartSipClick = onStartSipClick,
                    )
                }
                item {
                    RewardsEarningsCalculator(cashbackFraction = state.ui.estimateCashbackFraction)
                }
                item {
                    RewardsReferralCodeCard(
                        code = state.ui.referralCode,
                        onCopyClick = {
                            clipboardManager.setText(AnnotatedString(state.ui.referralCode))
                            scope.launch { snackbarHostState.showSnackbar("Referral code copied") }
                        },
                    )
                }
                if (state.errorMessage != null) {
                    item {
                        RewardsInlineRetryCard(
                            message = state.errorMessage,
                            onRetry = onRefresh,
                        )
                    }
                }
            }
        }
    }

    if (showQrDialog) {
        RewardsReferralQrDialog(
            referralCode = state.ui.referralCode,
            onShare = shareInvite,
            onDismiss = { showQrDialog = false },
        )
    }
}

@Composable
private fun RewardsReferDetailLoading(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Neutral05)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        repeat(4) {
            RewardsShimmerCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (it == 0) 188.dp else 160.dp),
                shape = RoundedCornerShape(20.dp),
            )
        }
    }
}
