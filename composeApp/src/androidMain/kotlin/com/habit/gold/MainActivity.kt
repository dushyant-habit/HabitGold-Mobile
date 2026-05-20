package com.habit.gold

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.platform.PlatformBridgeStore
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.storage.initializePlatformStorage
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutCoordinator
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutHost
import com.habit.gold.payments.juspay.JuspayActivityForwarder
import com.habit.gold.payments.juspay.JuspayPaymentResult
import com.google.firebase.messaging.FirebaseMessaging
import androidx.lifecycle.lifecycleScope
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.habit.gold.core.platform.extractReferralCodeFromUrl
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.json.JSONObject

class MainActivity : FragmentActivity(), EmbeddedJuspayCheckoutHost {
    private lateinit var embeddedJuspayCoordinator: EmbeddedJuspayCheckoutCoordinator
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HabitGold)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializePlatformStorage(applicationContext)
        startKoinIfNeeded()
        embeddedJuspayCoordinator = EmbeddedJuspayCheckoutCoordinator(this)
        embeddedJuspayCoordinator.schedulePrewarm()
        requestNotificationPermissionIfNeeded()
        captureReferralFromIntent(intent)
        captureInstallReferrerOnce()
        syncCurrentFcmToken()

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        captureReferralFromIntent(intent)
    }

    override fun startEmbeddedJuspayCheckout(
        sdkPayload: JSONObject,
        onResult: (JuspayPaymentResult) -> Unit,
    ): Boolean {
        return embeddedJuspayCoordinator.startCheckout(sdkPayload, onResult)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        JuspayActivityForwarder.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        JuspayActivityForwarder.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        if (::embeddedJuspayCoordinator.isInitialized) {
            embeddedJuspayCoordinator.release()
        }
        super.onDestroy()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun syncCurrentFcmToken() {
        val koin = GlobalContext.getOrNull() ?: return
        val manager = koin.get<DeviceTokenSyncManager>()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            lifecycleScope.launch {
                manager.registerTokenIfLoggedIn(token)
            }
        }
    }

    private fun captureReferralFromIntent(intent: Intent?) {
        val data = intent?.data ?: return
        val referralCode = extractReferralCodeFromUrl(data.toString()) ?: return
        val koin = GlobalContext.getOrNull() ?: return
        val store = koin.get<PlatformBridgeStore>()
        lifecycleScope.launch {
            store.writePendingReferralCode(referralCode)
        }
    }

    private fun captureInstallReferrerOnce() {
        val koin = GlobalContext.getOrNull() ?: return
        val store = koin.get<PlatformBridgeStore>()
        lifecycleScope.launch {
            if (store.readInstallReferrerCaptured()) return@launch

            val client = InstallReferrerClient.newBuilder(applicationContext).build()
            client.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val referrer = runCatching { client.installReferrer.installReferrer }.getOrNull()
                            val referralCode = referrer
                                ?.substringAfter("code=", "")
                                ?.substringBefore('&')
                                ?.takeIf { it.isNotBlank() }
                            lifecycleScope.launch {
                                val globalStore = GlobalContext.get().get<PlatformBridgeStore>()
                                val existingReferral = globalStore.readPendingReferralCode()
                                if (referralCode != null && existingReferral.isNullOrBlank()) {
                                    globalStore.writePendingReferralCode(referralCode)
                                }
                                globalStore.writeInstallReferrerCaptured(true)
                            }
                            client.endConnection()
                        }
                        InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                            lifecycleScope.launch {
                                GlobalContext.get().get<PlatformBridgeStore>().writeInstallReferrerCaptured(true)
                            }
                            client.endConnection()
                        }
                        InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> client.endConnection()
                        else -> client.endConnection()
                    }
                }

                override fun onInstallReferrerServiceDisconnected() = Unit
            })
        }
    }
}
