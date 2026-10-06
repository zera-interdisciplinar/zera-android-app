package com.zera.android.model.remote.service

import com.zera.android.model.dto.recycling.RecyclingBusinessResponseDTO
import retrofit2.http.GET

interface RecyclingsService {
    @GET("recyclings")
    suspend fun list(): List<RecyclingBusinessResponseDTO>
}
