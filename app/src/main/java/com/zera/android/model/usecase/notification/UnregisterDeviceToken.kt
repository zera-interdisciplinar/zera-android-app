package com.zera.android.model.usecase.notification

import com.zera.android.model.dto.notification.DeviceTokenRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient

class UnregisterDeviceToken {
    suspend fun execute() {
        val token = SqliteManager.getFcmToken()?.takeIf { it.isNotBlank() } ?: return
        if (!AdmCoreClient.isReady() || SqliteManager.getAccessToken().isNullOrBlank()) return
        AdmCoreClient.selfUserService.deleteDeviceToken(DeviceTokenRequestDTO(token = token))
    }
}
