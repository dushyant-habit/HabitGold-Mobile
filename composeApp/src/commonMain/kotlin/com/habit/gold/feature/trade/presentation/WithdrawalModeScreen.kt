package com.habit.gold.feature.trade.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_proceed
import habitgoldmobile.composeapp.generated.resources.common_selected
import habitgoldmobile.composeapp.generated.resources.trade_withdrawal_mode_cash_description
import habitgoldmobile.composeapp.generated.resources.trade_withdrawal_mode_cash_title
import habitgoldmobile.composeapp.generated.resources.trade_withdrawal_mode_coin_description
import habitgoldmobile.composeapp.generated.resources.trade_withdrawal_mode_coin_title
import habitgoldmobile.composeapp.generated.resources.trade_withdrawal_mode_title
import org.jetbrains.compose.resources.stringResource

private enum class TradeWithdrawalMode {
    Coin,
    Cash,
}

private val WithdrawalTopBarTitleColor = Color(0xFF1E293B)
private val WithdrawalUnselectedBorder = Color(0xFFE5E7EB)
private val WithdrawalCoinBackground = Color(0xFFFFF4D6)
private val WithdrawalCoinTint = Color(0xFFF59E0B)
private val WithdrawalCashBackground = Color(0xFFE8F6EC)
private val WithdrawalCashTint = Color(0xFF16A34A)
private val WithdrawalDescriptionText = Color(0xFF6B7280)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalModeScreen(
    onBackClick: () -> Unit,
    onNavigateToCoinMode: () -> Unit,
    onNavigateToCashMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedMode by rememberSaveable { mutableStateOf(TradeWithdrawalMode.Cash) }

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trade_withdrawal_mode_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WithdrawalTopBarTitleColor,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.common_back),
                            tint = Color.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                ) {
                    Button(
                        onClick = {
                            when (selectedMode) {
                                TradeWithdrawalMode.Coin -> onNavigateToCoinMode()
                                TradeWithdrawalMode.Cash -> onNavigateToCashMode()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HabitGoldPalette.plum,
                            disabledContainerColor = Color.LightGray,
                        ),
                    ) {
                        Text(
                            text = stringResource(Res.string.common_proceed),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WithdrawalModeCard(
                title = stringResource(Res.string.trade_withdrawal_mode_coin_title),
                description = stringResource(Res.string.trade_withdrawal_mode_coin_description),
                icon = Icons.Default.Toll,
                iconTint = WithdrawalCoinTint,
                iconBackground = WithdrawalCoinBackground,
                isSelected = selectedMode == TradeWithdrawalMode.Coin,
                onClick = { selectedMode = TradeWithdrawalMode.Coin },
            )

            WithdrawalModeCard(
                title = stringResource(Res.string.trade_withdrawal_mode_cash_title),
                description = stringResource(Res.string.trade_withdrawal_mode_cash_description),
                icon = Icons.Default.AccountBalanceWallet,
                iconTint = WithdrawalCashTint,
                iconBackground = WithdrawalCashBackground,
                isSelected = selectedMode == TradeWithdrawalMode.Cash,
                onClick = { selectedMode = TradeWithdrawalMode.Cash },
            )
        }
    }
}

@Composable
private fun WithdrawalModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) HabitGoldPalette.plum else WithdrawalUnselectedBorder,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = WithdrawalDescriptionText,
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) HabitGoldPalette.plum else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) HabitGoldPalette.plum else Color.LightGray,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(Res.string.common_selected),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}
