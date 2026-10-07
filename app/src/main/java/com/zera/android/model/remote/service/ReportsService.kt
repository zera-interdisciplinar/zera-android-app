package com.zera.android.model.remote.service

import com.zera.android.model.dto.ai.CreateDisposalReportRequestDTO
import com.zera.android.model.dto.ai.DisposalReportResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportsService {
    @POST("api/v1/reports")
    suspend fun createReport(
        @Body body: CreateDisposalReportRequestDTO,
    ): DisposalReportResponseDTO

    @GET("api/v1/reports/{disposalId}")
    suspend fun getReport(
        @Path("disposalId") disposalId: String,
    ): DisposalReportResponseDTO
}
