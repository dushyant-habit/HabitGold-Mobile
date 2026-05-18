package com.habit.gold.feature.alerts.domain.model

data class AlertItem(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: String,
    val isRead: Boolean,
)
