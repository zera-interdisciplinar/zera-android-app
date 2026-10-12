package com.zera.android.model.remote.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zera.android.MainActivity
import com.zera.android.R
import com.zera.android.model.usecase.notification.RegisterDeviceToken
import com.zera.android.view.navigation.PendingAlertDeepLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ZeraFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch {
            runCatching { RegisterDeviceToken().execute(token) }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body.orEmpty()
        showNotification(
            title = title,
            body = body,
            alertId = message.data[PendingAlertDeepLink.ExtraAlertId],
            kind = message.data[PendingAlertDeepLink.ExtraKind],
            severity = message.data[PendingAlertDeepLink.ExtraSeverity],
        )
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun showNotification(
        title: String,
        body: String,
        alertId: String?,
        kind: String?,
        severity: String?,
    ) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                ChannelId,
                "Alertas",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
        val tap = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(PendingAlertDeepLink.ExtraAlertId, alertId)
            putExtra(PendingAlertDeepLink.ExtraKind, kind)
            putExtra(PendingAlertDeepLink.ExtraSeverity, severity)
        }
        val pending = PendingIntent.getActivity(
            this,
            title.hashCode(),
            tap,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        manager.notify(
            title.hashCode(),
            NotificationCompat.Builder(this, ChannelId)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build(),
        )
    }

    companion object {
        const val ChannelId = "zera_alerts"
    }
}
