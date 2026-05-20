package com.habit.gold.feature.auth.presentation.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

private val SixDigitOtpRegex = Regex("""\b\d{6}\b""")

@Composable
actual fun PlatformOtpAutoFillEffect(
    enabled: Boolean,
    onOtpReceived: (String) -> Unit,
) {
    if (!enabled) return
    val context = LocalContext.current

    DisposableEffect(context, enabled) {
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras.getParcelable(SmsRetriever.EXTRA_STATUS)
                } ?: return
                if (status.statusCode != CommonStatusCodes.SUCCESS) return
                val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                val otp = SixDigitOtpRegex.find(message)?.value ?: return
                onOtpReceived(otp)
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            ContextCompat.RECEIVER_EXPORTED,
        )

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}
