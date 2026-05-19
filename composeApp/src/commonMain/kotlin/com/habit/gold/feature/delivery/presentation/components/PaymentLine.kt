package com.habit.gold.feature.delivery.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors

@Composable
fun PaymentLine(
    label: String,
    value: String,
    emphasize: Boolean = false,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (emphasize) 15.sp else 14.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) AppColors.Slate950 else AppColors.Slate500
        )
        Text(
            text = value,
            fontSize = if (emphasize) 20.sp else 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor ?: if (emphasize) AppColors.Primary else AppColors.Slate950
        )
    }
}
