@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.habit.gold.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.presentation.ChildCardBorder
import com.habit.gold.feature.home.presentation.ChildMutedText
import com.habit.gold.feature.home.presentation.ChildPrimaryText
import com.habit.gold.feature.home.presentation.HomeChildEmptyState
import com.habit.gold.feature.trade.domain.model.TradeUserVpa
import com.habit.gold.feature.trade.domain.usecase.GetTradeUserVpasUseCase
import com.habit.gold.feature.trade.domain.usecase.SetDefaultTradeVpaUseCase
import com.habit.gold.feature.trade.domain.usecase.VerifyTradeVpaUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.common_retry
import habitgoldmobile.composeapp.generated.resources.profile_vpa_default
import habitgoldmobile.composeapp.generated.resources.profile_vpa_empty
import habitgoldmobile.composeapp.generated.resources.profile_vpa_info_note
import habitgoldmobile.composeapp.generated.resources.profile_vpa_not_verified
import habitgoldmobile.composeapp.generated.resources.profile_vpa_verified
import habitgoldmobile.composeapp.generated.resources.trade_route_vpa_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProfileVpaListRoute(
    getTradeUserVpasUseCase: GetTradeUserVpasUseCase,
    setDefaultTradeVpaUseCase: SetDefaultTradeVpaUseCase,
    verifyTradeVpaUseCase: VerifyTradeVpaUseCase,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var refreshKey by rememberSaveable { mutableStateOf(0) }
    val uiState by produceState<ProfileVpaUiState>(
        initialValue = ProfileVpaUiState.Loading,
        refreshKey,
        getTradeUserVpasUseCase,
    ) {
        value = ProfileVpaUiState.Loading
        value = when (val result = getTradeUserVpasUseCase()) {
            is ApiResult.Success -> ProfileVpaUiState.Success(result.value)
            is ApiResult.Failure -> ProfileVpaUiState.Error(result.error.message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trade_route_vpa_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val state = uiState) {
                ProfileVpaUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 20.dp, bottom = 124.dp),
                    ) {
                        items(3) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.62f)
                                            .height(18.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.38f)
                                            .height(14.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = Color(0xFFE2E8F0))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(104.dp)
                                            .height(14.dp)
                                            .profileShimmerPlaceholder(RoundedCornerShape(999.dp)),
                                    )
                                }
                            }
                        }
                    }
                }

                is ProfileVpaUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = state.message,
                                color = ProfileError,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                            )
                            Button(
                                onClick = { refreshKey += 1 },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HabitGoldPalette.plum),
                            ) {
                                Text(
                                    text = stringResource(Res.string.common_retry),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                is ProfileVpaUiState.Success -> {
                    if (state.vpas.isEmpty()) {
                        HomeChildEmptyState(
                            paddingValues = PaddingValues(24.dp),
                            message = stringResource(Res.string.profile_vpa_empty),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 20.dp, bottom = 124.dp),
                        ) {
                            items(state.vpas, key = { it.id }) { vpa ->
                                ProfileVpaCard(vpa = vpa)
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, ChildCardBorder),
            ) {
                Text(
                    text = stringResource(Res.string.profile_vpa_info_note),
                    fontSize = 13.sp,
                    color = ChildMutedText,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                )
            }
        }
    }
}

@Composable
private fun ProfileVpaCard(
    vpa: TradeUserVpa,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vpa.address,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChildPrimaryText,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = vpa.holderName?.takeIf { it.isNotBlank() } ?: "-",
                        fontSize = 13.sp,
                        color = ChildMutedText,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = if (vpa.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (vpa.isVerified) Color(0xFF15803D) else Color(0xFFB45309),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (vpa.isVerified) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                    border = BorderStroke(
                        1.dp,
                        if (vpa.isVerified) Color(0xFFBBF7D0) else Color(0xFFFDE68A),
                    ),
                ) {
                    Text(
                        text = if (vpa.isVerified) {
                            stringResource(Res.string.profile_vpa_verified)
                        } else {
                            stringResource(Res.string.profile_vpa_not_verified)
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (vpa.isVerified) Color(0xFF15803D) else Color(0xFFB45309),
                    )
                }

                if (vpa.isDefault) {
                    Text(
                        text = stringResource(Res.string.profile_vpa_default),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HabitGoldPalette.plum,
                    )
                }
            }
        }
    }
}

private sealed interface ProfileVpaUiState {
    data object Loading : ProfileVpaUiState
    data class Success(val vpas: List<TradeUserVpa>) : ProfileVpaUiState
    data class Error(val message: String) : ProfileVpaUiState
}
