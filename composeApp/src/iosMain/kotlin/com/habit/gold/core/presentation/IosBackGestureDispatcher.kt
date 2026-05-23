package com.habit.gold.core.presentation

internal object IosBackGestureDispatcher {
    private data class HandlerEntry(
        var enabled: Boolean,
        var onBack: () -> Unit,
    )

    private var nextToken: Long = 0
    private val handlers = linkedMapOf<Long, HandlerEntry>()

    fun register(
        enabled: Boolean,
        onBack: () -> Unit,
    ): Long {
        val token = ++nextToken
        handlers[token] = HandlerEntry(
            enabled = enabled,
            onBack = onBack,
        )
        return token
    }

    fun update(
        token: Long,
        enabled: Boolean,
        onBack: () -> Unit,
    ) {
        handlers[token]?.apply {
            this.enabled = enabled
            this.onBack = onBack
        }
    }

    fun unregister(token: Long) {
        handlers.remove(token)
    }

    fun canHandleBackGesture(): Boolean {
        return handlers.values.lastOrNull { it.enabled } != null
    }

    fun handleBackGesture(): Boolean {
        val handler = handlers.values.lastOrNull { it.enabled } ?: return false
        handler.onBack()
        return true
    }
}

object IosBackGestureBridgeApi {
    fun canHandleBackGesture(): Boolean = IosBackGestureDispatcher.canHandleBackGesture()

    fun handleBackGesture(): Boolean = IosBackGestureDispatcher.handleBackGesture()
}
