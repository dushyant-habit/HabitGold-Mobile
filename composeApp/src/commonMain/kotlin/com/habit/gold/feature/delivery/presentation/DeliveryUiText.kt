package com.habit.gold.feature.delivery.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

sealed interface DeliveryUiText {
    data class Dynamic(val value: String) : DeliveryUiText
    data class Resource(
        val id: StringResource,
        val args: List<Any> = emptyList(),
    ) : DeliveryUiText
}

@Composable
fun DeliveryUiText.resolve(): String = when (this) {
    is DeliveryUiText.Dynamic -> value
    is DeliveryUiText.Resource -> stringResource(id, *args.toTypedArray())
}

suspend fun DeliveryUiText.resolveSuspending(): String = when (this) {
    is DeliveryUiText.Dynamic -> value
    is DeliveryUiText.Resource -> getString(id, *args.toTypedArray())
}
