package com.habit.gold.payments.juspay

import android.content.Intent
import android.content.IntentSender
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.habit.gold.BuildConfig
import org.json.JSONObject
import java.util.UUID
import `in`.juspay.hyperinteg.HyperServiceHolder
import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.ActivityLaunchDelegate
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import `in`.juspay.hypersdk.ui.IntentSenderDelegate

class JuspayCheckoutHelper(
    private val activity: FragmentActivity,
    private val dismissEmbeddedUiOnExternalLaunch: Boolean = false,
    private val resultCallback: ((JuspayPaymentResult) -> Unit)? = null,
) {
    private val hyperServiceHolder = HyperServiceHolder(activity)
    private var pendingSdkPayload: JSONObject? = null
    private var lastDeliveredResult: JuspayPaymentResult? = null
    private var lastDeliveredAt = 0L

    init {
        if (dismissEmbeddedUiOnExternalLaunch) {
            configureExternalLaunchDelegates()
        }
    }

    fun initiate() {
        if (hyperServiceHolder.isInitialised()) return
        hyperServiceHolder.setCallback(createPaymentsCallback())
        hyperServiceHolder.initiate(buildInitiatePayload())
        logInfo("Juspay initiate() called")
    }

    fun startCheckout(sdkPayload: JSONObject): Boolean {
        pendingSdkPayload = sdkPayload
        if (hyperServiceHolder.isInitialised()) {
            processPendingCheckout()
            return true
        }
        initiate()
        if (hyperServiceHolder.isInitialised()) {
            processPendingCheckout()
        }
        return true
    }

    fun process(sdkPayload: JSONObject): Boolean {
        if (!hyperServiceHolder.isInitialised()) {
            logWarn("process() called before Juspay SDK was initialised")
            return false
        }
        markNewProcess()
        hyperServiceHolder.process(sdkPayload)
        logInfo("Juspay process() called")
        return true
    }

    fun isReady(): Boolean = hyperServiceHolder.isInitialised()

    fun onBackPressed(): Boolean = hyperServiceHolder.onBackPressed()

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        hyperServiceHolder.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        hyperServiceHolder.hyperServices.onRequestPermissionsResult(
            requestCode,
            Array(permissions.size) { permissions[it] },
            grantResults,
        )
    }

    fun terminate() {
        pendingSdkPayload = null
        lastDeliveredResult = null
        lastDeliveredAt = 0L
        hyperServiceHolder.terminate()
    }

    private fun processPendingCheckout() {
        val payload = pendingSdkPayload ?: return
        pendingSdkPayload = null
        markNewProcess()
        hyperServiceHolder.process(payload)
        logInfo("Juspay process() called")
    }

    private fun buildInitiatePayload(): JSONObject {
        val payload = JSONObject().apply {
            put("action", "initiate")
            put("merchantId", BuildConfig.JUSPAY_MERCHANT_ID)
            put("clientId", BuildConfig.JUSPAY_CLIENT_ID)
            if (BuildConfig.JUSPAY_ROUTING_ID.isNotBlank()) {
                put("xRoutingId", BuildConfig.JUSPAY_ROUTING_ID)
            }
            put("environment", BuildConfig.JUSPAY_ENVIRONMENT)
        }

        return JSONObject().apply {
            put("requestId", UUID.randomUUID().toString())
            put("service", "in.juspay.hyperpay")
            put("payload", payload)
        }
    }

    private fun createPaymentsCallback(): HyperPaymentsCallbackAdapter {
        return object : HyperPaymentsCallbackAdapter() {
            override fun onEvent(jsonObject: JSONObject, responseHandler: JuspayResponseHandler?) {
                try {
                    when (jsonObject.optString("event")) {
                        "initiate_result" -> processPendingCheckout()
                        "process_result" -> deliverProcessResult(parseProcessResult(jsonObject))
                        "hide_loader" -> Unit
                    }
                } catch (error: Exception) {
                    logError("Error processing Juspay callback", error)
                }
            }
        }
    }

    private fun markNewProcess() {
        lastDeliveredResult = null
        lastDeliveredAt = 0L
    }

    private fun deliverProcessResult(result: JuspayPaymentResult) {
        val now = SystemClock.elapsedRealtime()
        val previousResult = lastDeliveredResult
        if (previousResult != null && now - lastDeliveredAt <= 600) {
            if (result == previousResult) {
                logWarn("Suppressing duplicate Juspay process_result")
                return
            }
            if (isDismissLike(previousResult) && isDismissLike(result)) {
                logWarn("Suppressing extra dismiss-like Juspay process_result")
                return
            }
        }
        lastDeliveredResult = result
        lastDeliveredAt = now
        resultCallback?.invoke(result)
    }

    private fun isDismissLike(result: JuspayPaymentResult): Boolean {
        return when (result) {
            JuspayPaymentResult.BackPressed -> true
            is JuspayPaymentResult.Failure -> {
                val status = result.status.lowercase()
                status == "backpressed" || status == "user_aborted" || "cancel" in status || "abort" in status
            }
            else -> false
        }
    }

    private fun parseProcessResult(jsonData: JSONObject): JuspayPaymentResult {
        return try {
            val error = jsonData.optBoolean("error", true)
            val payload = jsonData.optJSONObject("payload") ?: jsonData
            val status = payload.optString("status", "").lowercase()
            val errorMessage = jsonData.optString(
                "errorMessage",
                payload.optString("errorMessage", "Payment $status"),
            )

            if (!error) {
                when (status) {
                    "charged", "cod_initiated", "auto_refunded" -> JuspayPaymentResult.Success(status)
                    "backpressed", "user_aborted" -> JuspayPaymentResult.BackPressed
                    else -> JuspayPaymentResult.Failure(status, errorMessage)
                }
            } else {
                when (status) {
                    "backpressed", "user_aborted" -> JuspayPaymentResult.BackPressed
                    else -> JuspayPaymentResult.Failure(status, errorMessage)
                }
            }
        } catch (error: Exception) {
            logError("Error parsing Juspay process_result", error)
            JuspayPaymentResult.Failure("error", "Something went wrong")
        }
    }

    private fun configureExternalLaunchDelegates() {
        hyperServiceHolder.getHyperServices().setActivityLaunchDelegate(
            object : ActivityLaunchDelegate {
                @Suppress("DEPRECATION")
                override fun startActivityForResult(intent: Intent, requestCode: Int, options: android.os.Bundle?) {
                    activity.startActivityForResult(intent, requestCode, options)
                    dismissEmbeddedCheckoutUi()
                }
            },
        )
        hyperServiceHolder.setIntentSenderDelegate(
            object : IntentSenderDelegate {
                @Suppress("DEPRECATION")
                override fun startIntentSenderForResult(
                    intentSender: IntentSender,
                    requestCode: Int,
                    fillInIntent: Intent?,
                    flagsMask: Int,
                    flagsValues: Int,
                    extraFlags: Int,
                    options: android.os.Bundle?,
                ) {
                    try {
                        activity.startIntentSenderForResult(
                            intentSender,
                            requestCode,
                            fillInIntent,
                            flagsMask,
                            flagsValues,
                            extraFlags,
                            options,
                        )
                        dismissEmbeddedCheckoutUi()
                    } catch (error: IntentSender.SendIntentException) {
                        logError("Unable to launch Juspay intent sender", error)
                    }
                }
            },
        )
    }

    private fun dismissEmbeddedCheckoutUi() {
        activity.runOnUiThread {
            val juspayFragments = activity.supportFragmentManager.fragments
                .filter { it.javaClass.name.startsWith("in.juspay.") }
            if (juspayFragments.isEmpty()) return@runOnUiThread
            activity.supportFragmentManager.beginTransaction().apply {
                juspayFragments.forEach(::remove)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }.commitAllowingStateLoss()
            activity.supportFragmentManager.executePendingTransactions()
            activity.supportFragmentManager.fragments
                .firstOrNull { it.javaClass.name.startsWith("in.juspay.") }
                ?.let { fragment ->
                    activity.supportFragmentManager.beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss()
                }
        }
    }

    private fun logInfo(message: String) {
        Log.i(TAG, message)
    }

    private fun logWarn(message: String) {
        Log.w(TAG, message)
    }

    private fun logError(message: String, error: Throwable) {
        Log.e(TAG, message, error)
    }

    companion object {
        private const val TAG = "JuspayCheckout"
    }
}
