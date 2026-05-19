package com.habit.gold.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.habit.gold.MainActivity
import com.habit.gold.R
import com.habit.gold.core.di.startKoinIfNeeded
import com.habit.gold.core.platform.notifications.DeviceTokenSyncManager
import com.habit.gold.core.platform.notifications.PlatformAlertRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HabitGoldMessagingService : FirebaseMessagingService(), KoinComponent {
    private val deviceTokenSyncManager: DeviceTokenSyncManager by inject()
    private val platformAlertRecorder: PlatformAlertRecorder by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        startKoinIfNeeded()
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch {
            deviceTokenSyncManager.registerTokenIfLoggedIn(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"] ?: "HabitGold"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        if (body.isNotBlank()) {
            showNotification(title, body)
        }
        scope.launch {
            platformAlertRecorder.record(title, body)
        }
    }

    private fun showNotification(title: String, body: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ),
            )
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private companion object {
        const val CHANNEL_ID = "habitgold_notifications"
        const val CHANNEL_NAME = "HabitGold Notifications"
    }
}
