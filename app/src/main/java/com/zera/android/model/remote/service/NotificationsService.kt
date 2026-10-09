package com.zera.android.model.remote.service

import com.zera.android.model.dto.notification.AlertResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationsService {
    @GET("notifications/alerts")
    suspend fun listAlerts(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): List<AlertResponseDTO>
}
