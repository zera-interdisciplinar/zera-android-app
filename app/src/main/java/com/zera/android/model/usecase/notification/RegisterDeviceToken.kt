package com.zera.android.model.usecase.notification

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.zera.android.model.dto.notification.DeviceTokenRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RegisterDeviceToken(
    private val tokenProvider: suspend () -> String? = {
        FirebaseMessaging.getInstance().token.awaitOrNull()
    },
) {
    suspend fun execute(token: String? = null) {
        val fcm = token?.takeIf { it.isNotBlank() } ?: tokenProvider()?.takeIf { it.isNotBlank() } ?: return
        SqliteManager.saveFcmToken(fcm)
        if (!AdmCoreClient.isReady() || SqliteManager.getAccessToken().isNullOrBlank()) return
        AdmCoreClient.selfUserService.registerDeviceToken(
            DeviceTokenRequestDTO(token = fcm, platform = "ANDROID"),
        )
    }
}

internal suspend fun <T> Task<T>.awaitOrNull(): T? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { value -> if (cont.isActive) cont.resume(value) }
        addOnFailureListener { if (cont.isActive) cont.resume(null) }
        addOnCanceledListener { if (cont.isActive) cont.resume(null) }
    }
