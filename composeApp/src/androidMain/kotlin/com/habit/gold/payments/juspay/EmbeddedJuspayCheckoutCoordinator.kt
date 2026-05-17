package com.habit.gold.payments.juspay

import androidx.fragment.app.FragmentActivity
import org.json.JSONObject

class EmbeddedJuspayCheckoutCoordinator(
    private val activity: FragmentActivity,
) {
    private var helper: JuspayCheckoutHelper? = null
    private var pendingResultHandler: ((JuspayPaymentResult) -> Unit)? = null
    private var isReleased = false
    private var prewarmPosted = false

    fun schedulePrewarm() {
        if (prewarmPosted || isReleased) return
        prewarmPosted = true
        activity.window?.decorView?.post {
            prewarmPosted = false
            prewarm()
        }
    }

    fun prewarm() {
        if (isReleased || activity.isFinishing || activity.isDestroyed) return
        ensureHelper().initiate()
    }

    fun startCheckout(
        sdkPayload: JSONObject,
        onResult: (JuspayPaymentResult) -> Unit,
    ): Boolean {
        if (isReleased || activity.isFinishing || activity.isDestroyed) return false
        pendingResultHandler = onResult
        val checkoutHelper = ensureHelper()
        return if (checkoutHelper.isReady()) {
            checkoutHelper.process(sdkPayload)
        } else {
            checkoutHelper.startCheckout(sdkPayload)
        }
    }

    fun release() {
        isReleased = true
        pendingResultHandler = null
        helper?.let { currentHelper ->
            JuspayActivityForwarder.detach(currentHelper)
            currentHelper.terminate()
        }
        helper = null
    }

    private fun ensureHelper(): JuspayCheckoutHelper {
        helper?.let { return it }
        return JuspayCheckoutHelper(
            activity = activity,
            dismissEmbeddedUiOnExternalLaunch = true,
        ) { result ->
            pendingResultHandler?.let { handler ->
                pendingResultHandler = null
                handler(result)
            }
        }.also { createdHelper ->
            helper = createdHelper
            JuspayActivityForwarder.attach(createdHelper)
        }
    }
}
