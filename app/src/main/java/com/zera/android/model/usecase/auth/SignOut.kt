package com.zera.android.model.usecase.auth

import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient
import com.zera.android.model.usecase.notification.UnregisterDeviceToken
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class SignOut {
    // Device token sai antes do logout: os dois usam o access token, que só é apagado no fim.
    // Falhas de rede não impedem o logout local.
    suspend fun execute() {
        runCatching { withTimeoutOrNull(5.seconds) { UnregisterDeviceToken().execute() } }
        runCatching { withTimeoutOrNull(5.seconds) { revokeRefreshToken() } }
        SingIn().clearLocalSession()
    }

    private suspend fun revokeRefreshToken() {
        if (!AdmCoreClient.isReady() || SqliteManager.getAccessToken().isNullOrBlank()) return
        AdmCoreClient.authService.logout()
    }
}
