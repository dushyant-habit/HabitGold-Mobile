package com.habit.gold.feature.rewards.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.habit.gold.core.designsystem.icons.HabitGoldIcons as Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.common_back
import habitgoldmobile.composeapp.generated.resources.rewards_flow_toolbar_rewards
import habitgoldmobile.composeapp.generated.resources.rewards_flow_toolbar_rewards_history
import org.jetbrains.compose.resources.stringResource

private val RewardsFlowToolbarHorizontalPadding = 4.dp
private val RewardsFlowToolbarVerticalPadding = 2.dp

@Composable
fun RewardsHomeTopBar(
    onRewardsHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Slate200,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(
                horizontal = RewardsFlowToolbarHorizontalPadding,
                vertical = RewardsFlowToolbarVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.rewards_flow_toolbar_rewards),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800,
            )
        }
        IconButton(onClick = onRewardsHistoryClick) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(Res.string.rewards_flow_toolbar_rewards_history),
                tint = Slate800,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun RewardsCenterTitleTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {
        Spacer(modifier = Modifier.size(48.dp))
    },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Slate200,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(
                horizontal = RewardsFlowToolbarHorizontalPadding,
                vertical = RewardsFlowToolbarVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.common_back),
                tint = Slate800,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Slate800,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            trailing()
        }
    }
}
