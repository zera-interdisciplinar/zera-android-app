package com.zera.android.model.remote.service

import com.zera.android.model.dto.inventory.DashboardHomeResponseDTO
import com.zera.android.model.dto.inventory.IndicatorsResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface InventoryService {
    @GET("api/v1/dashboard/home")
    suspend fun getHome(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5,
    ): DashboardHomeResponseDTO

    @GET("api/v1/dashboard/indicators")
    suspend fun getIndicators(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
    ): IndicatorsResponseDTO
}
