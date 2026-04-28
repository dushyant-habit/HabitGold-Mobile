package com.habit.gold.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habit.gold.core.config.BootstrapCheck
import com.habit.gold.core.presentation.AppCard

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading || uiState.bootstrapInfo == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val bootstrapInfo = uiState.bootstrapInfo
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5E9C9),
                        Color(0xFFFFFBF2),
                        Color(0xFFF2F4F7),
                    ),
                ),
            )
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = bootstrapInfo.appConfig.appName,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF221A11),
        )
        Text(
            text = "Production KMP starter for Android and iOS.",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF5A4A36),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickFact(
                label = "Platform",
                value = "${bootstrapInfo.platformInfo.name} ${bootstrapInfo.platformInfo.version}",
                modifier = Modifier.weight(1f),
            )
            QuickFact(
                label = "Environment",
                value = bootstrapInfo.appConfig.environment.name,
                modifier = Modifier.weight(1f),
            )
        }

        AppCard(
            title = "Base URL",
            subtitle = bootstrapInfo.appConfig.normalizedBaseUrl,
            accent = listOf(Color(0xFFFFF4D6), Color(0xFFF1DFC3)),
        )

        SectionTitle("What is already wired")
        bootstrapInfo.checks.forEachIndexed { index, check ->
            val accent = when (index % 4) {
                0 -> listOf(Color(0xFFFEF2D0), Color(0xFFF7E5B8))
                1 -> listOf(Color(0xFFE7F3EE), Color(0xFFD4E6DD))
                2 -> listOf(Color(0xFFE8EEF8), Color(0xFFD7E1F0))
                else -> listOf(Color(0xFFF4EAF6), Color(0xFFE6D8E9))
            }
            CheckCard(check = check, accent = accent)
        }

        SectionTitle("Recommended next steps")
        bootstrapInfo.nextSteps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF221A11))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF2C2218),
                )
            }
        }
    }
}

@Composable
private fun CheckCard(
    check: BootstrapCheck,
    accent: List<Color>,
) {
    val badgeText = check.title.first().toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(accent))
            .padding(18.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.88f))
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4D3A20),
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = check.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF23180B),
            )
            Text(
                text = check.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5D4B37),
            )
        }
    }
}

@Composable
private fun QuickFact(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.82f))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF7C6749),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF20170F),
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF2A2118),
    )
}
