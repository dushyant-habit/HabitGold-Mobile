package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.swipe_to_redeem_track_icon_description
import habitgoldmobile.composeapp.generated.resources.swipe_to_redeem_track_label
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

private val TrackPurple = Purple900Soft
private val AccentPurple = Purple650

@Composable
fun RewardsSwipeToRedeemTrack(
    onSwipeComplete: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    var trackWidthPx by remember { mutableStateOf(0f) }
    var knobOuterWidthPx by remember { mutableStateOf(0f) }
    val horizontalMarginPx = 16f
    val swipeableWidth = (trackWidthPx - knobOuterWidthPx - horizontalMarginPx).coerceAtLeast(0f)

    var offsetX by remember { mutableStateOf(0f) }
    val progress = if (swipeableWidth > 0f) (offsetX / swipeableWidth).coerceIn(0f, 1f) else 0f

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            offsetX = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(TrackPurple)
            .onSizeChanged { trackWidthPx = it.width.toFloat() },
    ) {
        Text(
            text = stringResource(Res.string.swipe_to_redeem_track_label),
            color = White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .alpha(1f - progress),
            textAlign = TextAlign.Center,
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .padding(4.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(White)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (!isLoading) {
                            offsetX = (offsetX + delta).coerceIn(0f, swipeableWidth)
                        }
                    },
                    onDragStopped = {
                        if (isLoading) return@draggable
                        if (offsetX >= swipeableWidth * 0.85f) {
                            onSwipeComplete()
                            offsetX = 0f
                        } else {
                            offsetX = 0f
                        }
                    },
                )
                .onSizeChanged { knobOuterWidthPx = it.width.toFloat() },
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = AccentPurple,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(Res.string.swipe_to_redeem_track_icon_description),
                    tint = AccentPurple,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
