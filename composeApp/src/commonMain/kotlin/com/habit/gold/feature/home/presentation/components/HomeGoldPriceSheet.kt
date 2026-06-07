package com.habit.gold.feature.home.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.HabitGoldPalette
import com.habit.gold.core.network.ApiResult
import com.habit.gold.feature.home.domain.model.HomeGoldPricePoint
import com.habit.gold.feature.home.domain.usecase.GetHomePriceHistoryUseCase
import com.habit.gold.feature.home.presentation.formatInr
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_close
import habitgoldmobile.composeapp.generated.resources.home_screen_buy_now_cta
import habitgoldmobile.composeapp.generated.resources.home_screen_fetching_live_price
import habitgoldmobile.composeapp.generated.resources.home_screen_gold_price_trend
import habitgoldmobile.composeapp.generated.resources.home_screen_past_range
import kotlin.math.max
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource

private data class HomePriceHistorySheetState(
    val pricePoints: List<HomeGoldPricePoint> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@Composable
internal fun HomeGoldPriceSheet(
    liveRate: Double,
    getHomePriceHistoryUseCase: GetHomePriceHistoryUseCase,
    onClose: () -> Unit,
    onBuyNow: () -> Unit,
) {
    var selectedRange by remember { mutableStateOf("1Y") }
    var selectedPointIndex by remember { mutableIntStateOf(-1) }
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
                pricePoints = result.value,
                isLoading = false,
            )
        }
    }
    val pricePoints = chartState.pricePoints
    val activePointIndex = when {
        selectedPointIndex in pricePoints.indices -> selectedPointIndex
        pricePoints.isNotEmpty() -> pricePoints.lastIndex
        else -> -1
    }
    val selectedPoint = pricePoints.getOrNull(activePointIndex)
    val displayedPrice = selectedPoint?.price ?: liveRate.takeIf { it > 0.0 } ?: pricePoints.lastOrNull()?.price ?: 0.0
    val firstPrice = pricePoints.firstOrNull()?.price ?: displayedPrice
    val priceDelta = displayedPrice - firstPrice
    val percentDelta = if (firstPrice > 0.0) (priceDelta / firstPrice) * 100.0 else 0.0
    val isPositiveTrend = priceDelta >= 0.0
    val selectedPointLabel = selectedPoint?.let { formatHomeSheetSelectionLabel(it.timestampMillis, selectedRange) } ?: "Latest price"

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
                    fontSize = 24.sp,
                    color = HabitGoldPalette.plum,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${if (isPositiveTrend) "+" else ""}${formatHomeSheetPercent(percentDelta)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositiveTrend) Color(0xFF15803D) else Color(0xFFD92D20),
                    )
                    Text(
                        text = "\u2022",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                    )
                    Text(
                        text = selectedPointLabel,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                    )
                }
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
                            selectedPointIndex = -1
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
            Text(
                text = chartState.errorMessage.orEmpty(),
                fontSize = 14.sp,
                color = Color(0xFF475569),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            if (pricePoints.isNotEmpty()) {
                HomeGoldPriceLineChart(
                    pricePoints = pricePoints,
                    selectedRange = selectedRange,
                    selectedPointIndex = activePointIndex,
                    onSelectPointIndex = { selectedPointIndex = it },
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
    pricePoints: List<HomeGoldPricePoint>,
    selectedRange: String,
    selectedPointIndex: Int,
    onSelectPointIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = Color(0xFF7B2CBF)
    val fillColor = Color(0xFF7B2CBF).copy(alpha = 0.10f)
    val gridColor = Color(0xFFE5E7EB)
    val minPrice = pricePoints.minOf { it.price }
    val maxPrice = pricePoints.maxOf { it.price }
    val xAxisLabels = remember(pricePoints, selectedRange) { buildXAxisLabels(pricePoints, selectedRange) }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pricePoints) {
                        detectTapGestures { offset ->
                            onSelectPointIndex(chartPointIndexFromOffset(offset.x, size.width.toFloat(), pricePoints.lastIndex))
                        }
                    }
                    .pointerInput(pricePoints) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                onSelectPointIndex(chartPointIndexFromOffset(offset.x, size.width.toFloat(), pricePoints.lastIndex))
                            },
                            onDrag = { change, _ ->
                                onSelectPointIndex(chartPointIndexFromOffset(change.position.x, size.width.toFloat(), pricePoints.lastIndex))
                            },
                        )
                    },
            ) {
                if (pricePoints.isEmpty()) return@Canvas

                val minPriceValue = minPrice.toFloat()
                val maxPriceValue = maxPrice.toFloat()
                val priceRange = max(maxPriceValue - minPriceValue, 1f)
                val spacing = if (pricePoints.size > 1) size.width / (pricePoints.size - 1).toFloat() else size.width

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
                pricePoints.forEachIndexed { index, point ->
                    val x = spacing * index
                    val yRatio = ((point.price.toFloat() - minPriceValue) / priceRange).coerceIn(0f, 1f)
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

                if (selectedPointIndex in pricePoints.indices) {
                    val selectedPoint = pricePoints[selectedPointIndex]
                    val x = spacing * selectedPointIndex
                    val yRatio = ((selectedPoint.price.toFloat() - minPriceValue) / priceRange).coerceIn(0f, 1f)
                    val y = size.height - (yRatio * (size.height - 12.dp.toPx())) - 6.dp.toPx()
                    drawLine(
                        color = lineColor.copy(alpha = 0.25f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                    drawCircle(color = Color.White, radius = 7.dp.toPx(), center = Offset(x, y))
                    drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            xAxisLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                )
            }
        }
    }
}

private fun formatHomeSheetInr(value: Double): String = formatInr(value)

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

private fun buildXAxisLabels(
    pricePoints: List<HomeGoldPricePoint>,
    selectedRange: String,
): List<String> {
    if (pricePoints.isEmpty()) return emptyList()
    val middleIndex = pricePoints.lastIndex / 2
    return listOf(
        formatHomeSheetXAxisLabel(pricePoints.first().timestampMillis, selectedRange),
        formatHomeSheetXAxisLabel(pricePoints[middleIndex].timestampMillis, selectedRange),
        formatHomeSheetXAxisLabel(pricePoints.last().timestampMillis, selectedRange),
    )
}

private fun chartPointIndexFromOffset(
    offsetX: Float,
    width: Float,
    lastIndex: Int,
): Int {
    if (lastIndex <= 0 || width <= 0f) return 0
    val segment = width / lastIndex.toFloat()
    return (offsetX / segment).toInt().coerceIn(0, lastIndex)
}

private fun formatHomeSheetSelectionLabel(
    timestampMillis: Long,
    selectedRange: String,
): String {
    val local = Instant.fromEpochMilliseconds(timestampMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    return when (selectedRange.uppercase()) {
        "1W", "1M" -> "${local.day} ${monthAbbreviation(local.month.name)}"
        else -> "${monthAbbreviation(local.month.name)} ${local.year}"
    }
}

private fun formatHomeSheetXAxisLabel(
    timestampMillis: Long,
    selectedRange: String,
): String {
    val local = Instant.fromEpochMilliseconds(timestampMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    return when (selectedRange.uppercase()) {
        "1W" -> "${local.day} ${monthAbbreviation(local.month.name)}"
        "1M" -> "${local.day} ${monthAbbreviation(local.month.name)}"
        "3M", "6M" -> "${monthAbbreviation(local.month.name)} ${local.year.toString().takeLast(2)}"
        else -> monthAbbreviation(local.month.name)
    }
}

private fun monthAbbreviation(monthName: String): String {
    return monthName.lowercase().replaceFirstChar { it.titlecase() }.take(3)
}
