package com.zera.android.model.usecase.notification

import com.zera.android.model.dto.notification.AlertResponseDTO
import com.zera.android.model.remote.client.AdmCoreClient

class GetAlerts {
    suspend fun execute(
        status: String? = null,
        page: Int = 0,
        size: Int = 20,
    ): List<AlertResponseDTO> =
        AdmCoreClient.notificationsService.listAlerts(status = status, page = page, size = size)
}
