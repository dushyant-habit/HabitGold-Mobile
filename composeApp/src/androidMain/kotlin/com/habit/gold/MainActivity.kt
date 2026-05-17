package com.habit.gold

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.storage.initializePlatformStorage
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutCoordinator
import com.habit.gold.payments.juspay.EmbeddedJuspayCheckoutHost
import com.habit.gold.payments.juspay.JuspayActivityForwarder
import com.habit.gold.payments.juspay.JuspayPaymentResult
import org.json.JSONObject

class MainActivity : FragmentActivity(), EmbeddedJuspayCheckoutHost {
    private lateinit var embeddedJuspayCoordinator: EmbeddedJuspayCheckoutCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HabitGold)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initializePlatformStorage(applicationContext)
        startKoinIfNeeded()
        embeddedJuspayCoordinator = EmbeddedJuspayCheckoutCoordinator(this)
        embeddedJuspayCoordinator.schedulePrewarm()

        setContent {
            App()
        }
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
}
