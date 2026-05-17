package com.habit.gold.feature.home.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.buy_gold_screen_buy_gold
import habitgoldmobile.composeapp.generated.resources.common_close
import habitgoldmobile.composeapp.generated.resources.home_screen_buy_now_cta
import habitgoldmobile.composeapp.generated.resources.home_screen_current_gold_price
import habitgoldmobile.composeapp.generated.resources.home_screen_fetching_live_price
import habitgoldmobile.composeapp.generated.resources.home_screen_gold_price_trend
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_buy_now
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_learn_more
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_next
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_button_start_saving
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_description_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_gold
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_growth
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_liquidity
import habitgoldmobile.composeapp.generated.resources.home_screen_intro_title_secure
import habitgoldmobile.composeapp.generated.resources.home_screen_past_range
import habitgoldmobile.composeapp.generated.resources.img_bis_safety
import habitgoldmobile.composeapp.generated.resources.img_habitgold_intro
import habitgoldmobile.composeapp.generated.resources.img_liquid_accessible
import habitgoldmobile.composeapp.generated.resources.img_proven_growth
import kotlin.math.max
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.launch
import com.habit.gold.feature.home.presentation.formatInr

internal sealed interface HomeBottomSheetState {
    data object GoldPrice : HomeBottomSheetState
    data class IntroPager(val initialPage: Int) : HomeBottomSheetState
    data class PendingAction(val message: String) : HomeBottomSheetState
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
            is HomeBottomSheetState.PendingAction -> HomePendingActionSheet(
                message = sheetState.message,
                onDismiss = onDismiss,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HomePendingActionSheet(
    message: String,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2CBF)),
        ) {
            Text(
                text = stringResource(Res.string.common_close),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private data class HomePriceHistorySheetState(
    val points: List<HomeGoldPricePoint> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@Composable
private fun HomeGoldPriceSheet(
    liveRate: Double,
    getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
    onClose: () -> Unit,
    onBuyNow: () -> Unit,
) {
    var selectedRange by remember { mutableStateOf("1Y") }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    val chartState by produceState(
        initialValue = HomePriceHistorySheetState(isLoading = true),
        key1 = selectedRange,
        key2 = liveRate,
    ) {
        value = HomePriceHistorySheetState(isLoading = true)
        value = when (val result = getHomePriceHistoryUseCase(selectedRange, liveRate)) {
            is ApiResult.Failure -> HomePriceHistorySheetState(
                isLoading = false,
                errorMessage = result.error.message,
            )
            is ApiResult.Success -> HomePriceHistorySheetState(
                points = result.value,
                isLoading = false,
            )
        }
    }
    val points = chartState.points
    val selectedPoint = points.getOrNull(selectedIndex)
    val displayedPrice = selectedPoint?.price ?: liveRate.takeIf { it > 0.0 } ?: points.lastOrNull()?.price ?: 0.0
    val firstPrice = points.firstOrNull()?.price ?: displayedPrice
    val priceDelta = displayedPrice - firstPrice
    val percentDelta = if (firstPrice > 0.0) (priceDelta / firstPrice) * 100.0 else 0.0
    val isPositive = priceDelta >= 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.home_screen_gold_price_trend),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${formatHomeSheetInr(displayedPrice)}",
                    fontSize = 18.sp,
                    color = if (selectedIndex >= 0) Color(0xFF7B2CBF) else Color(0xFF64748B),
                    fontWeight = if (selectedIndex >= 0) FontWeight.Bold else FontWeight.Normal,
                )
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFF5F5F5), CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.common_close),
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("1W", "1M", "3M", "6M", "1Y").forEach { range ->
                val isSelected = range == selectedRange
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF7B2CBF) else Color.Transparent)
                        .clickable {
                            selectedRange = range
                            selectedIndex = -1
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = range,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF64748B),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (chartState.isLoading) {
            Text(
                text = stringResource(Res.string.home_screen_fetching_live_price),
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else if (!chartState.errorMessage.isNullOrBlank()) {
            val errorMessage = chartState.errorMessage.orEmpty()
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Color(0xFF475569),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Text(
                text = stringResource(Res.string.home_screen_current_gold_price),
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${formatHomeSheetInr(liveRate.takeIf { it > 0.0 } ?: displayedPrice)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = if (isPositive) Color(0xFF15803D) else Color(0xFFD92D20),
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "${if (isPositive) "+" else ""}${formatHomeSheetPercent(percentDelta)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) Color(0xFF15803D) else Color(0xFFD92D20),
                )
                Text(
                    text = stringResource(Res.string.home_screen_past_range, selectedRange),
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (points.isNotEmpty()) {
                HomeGoldPriceLineChart(
                    points = points,
                    selectedIndex = selectedIndex,
                    onSelectIndex = { selectedIndex = it },
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onBuyNow,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2CBF)),
        ) {
            Text(
                text = stringResource(Res.string.home_screen_buy_now_cta),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun HomeGoldPriceLineChart(
    points: List<HomeGoldPricePoint>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = Color(0xFF7B2CBF)
    val fillColor = Color(0xFF7B2CBF).copy(alpha = 0.10f)
    val gridColor = Color(0xFFE5E7EB)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .pointerInput(points) {
                detectTapGestures { offset ->
                    if (points.size <= 1) return@detectTapGestures
                    val segment = size.width / (points.size - 1).toFloat()
                    val index = (offset.x / segment).toInt().coerceIn(0, points.lastIndex)
                    onSelectIndex(index)
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (points.isEmpty()) return@Canvas

            val minPrice = points.minOf { it.price }.toFloat()
            val maxPrice = points.maxOf { it.price }.toFloat()
            val priceRange = max(maxPrice - minPrice, 1f)
            val spacing = if (points.size > 1) size.width / (points.size - 1) else size.width

            repeat(4) { index ->
                val y = (size.height / 3f) * index
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            val linePath = Path()
            val fillPath = Path()
            points.forEachIndexed { index, point ->
                val x = spacing * index
                val yRatio = ((point.price.toFloat() - minPrice) / priceRange).coerceIn(0f, 1f)
                val y = size.height - (yRatio * (size.height - 12.dp.toPx())) - 6.dp.toPx()
                if (index == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, size.height)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }
            fillPath.lineTo(size.width, size.height)
            fillPath.close()

            drawPath(path = fillPath, color = fillColor)
            drawPath(
                path = linePath,
                brush = SolidColor(lineColor),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
            )

            if (selectedIndex in points.indices) {
                val selectedPoint = points[selectedIndex]
                val x = spacing * selectedIndex
                val yRatio = ((selectedPoint.price.toFloat() - minPrice) / priceRange).coerceIn(0f, 1f)
                val y = size.height - (yRatio * (size.height - 12.dp.toPx())) - 6.dp.toPx()
                drawCircle(color = Color.White, radius = 7.dp.toPx(), center = Offset(x, y))
                drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}

private data class HomeIntroPagerItem(
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
    val buttonText: StringResource,
)

@Composable
private fun HomeIntroPagerSheet(
    initialPage: Int,
    onPrimaryAction: () -> Unit,
) {
    val items = remember {
        listOf(
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_gold,
                description = Res.string.home_screen_intro_description_gold,
                image = Res.drawable.img_habitgold_intro,
                buttonText = Res.string.home_screen_intro_button_next,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_secure,
                description = Res.string.home_screen_intro_description_secure,
                image = Res.drawable.img_bis_safety,
                buttonText = Res.string.home_screen_intro_button_learn_more,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_liquidity,
                description = Res.string.home_screen_intro_description_liquidity,
                image = Res.drawable.img_liquid_accessible,
                buttonText = Res.string.home_screen_intro_button_start_saving,
            ),
            HomeIntroPagerItem(
                title = Res.string.home_screen_intro_title_growth,
                description = Res.string.home_screen_intro_description_growth,
                image = Res.drawable.img_proven_growth,
                buttonText = Res.string.home_screen_intro_button_buy_now,
            ),
        )
    }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 20.dp, end = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                val item = items[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(item.title).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFFF7F2FA)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(item.image),
                            contentDescription = null,
                            modifier = Modifier.size(180.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(36.dp))
                    Text(
                        text = stringResource(item.description),
                        fontSize = 16.sp,
                        color = Color(0xFF4A4A4A),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(items.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF7B2CBF) else Color(0x337B2CBF))
                        .size(height = 6.dp, width = if (isSelected) 24.dp else 6.dp),
                )
            }
        }

        val currentItem = items[pagerState.currentPage]
        Button(
            onClick = {
                if (pagerState.currentPage < items.lastIndex) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onPrimaryAction()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7B2CBF),
                contentColor = Color.White,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = stringResource(currentItem.buttonText),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun formatHomeSheetInr(value: Double): String {
    return formatInr(value)
}

private fun formatHomeSheetPercent(value: Double): String {
    val rounded = kotlin.math.abs((value * 100).toInt() / 100.0)
    val whole = rounded.toInt()
    val decimals = (((rounded - whole) * 100).toInt())
    return if (decimals == 0) {
        whole.toString()
    } else {
        "$whole.${decimals.toString().padStart(2, '0')}"
    }
}
