package com.habit.gold.feature.home.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase

internal sealed interface HomeBottomSheetState {
    data object GoldPrice : HomeBottomSheetState
    data class IntroPager(val initialPage: Int) : HomeBottomSheetState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeBottomSheetHost(
    sheetState: HomeBottomSheetState,
    liveRate: Double,
    getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
    onDismiss: () -> Unit,
    onBuyGoldClick: () -> Unit,
) {
    val modalState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalState,
        containerColor = Color.White,
        tonalElevation = 0.dp,
    ) {
        when (sheetState) {
            HomeBottomSheetState.GoldPrice -> HomeGoldPriceSheet(
                liveRate = liveRate,
                getHomePriceHistoryUseCase = getHomePriceHistoryUseCase,
                onClose = onDismiss,
                onBuyNow = {
                    onDismiss()
                    onBuyGoldClick()
                },
            )

            is HomeBottomSheetState.IntroPager -> HomeIntroPagerSheet(
                initialPage = sheetState.initialPage,
                onPrimaryAction = {
                    onDismiss()
                    onBuyGoldClick()
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
