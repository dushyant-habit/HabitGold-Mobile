package com.habit.gold.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun DesignSystemPreview() {
    HabitGoldTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(HabitGoldDesignSystem.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(HabitGoldDesignSystem.spacing.md),
        ) {
            Text(
                text = "HabitGold Design System",
                style = MaterialTheme.typography.headlineMedium,
            )
            AppInfoChip(label = "24K Gold")
            AppSectionCard {
                Text(
                    text = "Reusable card surface",
                    style = MaterialTheme.typography.titleLarge,
                )
                AppSupportingText("Shared spacing, color, and shape tokens are now centralized.")
            }
            AppPrimaryButton(label = "Primary Action", onClick = {})
        }
    }
}
