package com.zera.android.model.remote.service

import com.zera.android.model.dto.telephone.CreateTelephoneRequestDTO
import com.zera.android.model.dto.telephone.TelephoneResponseDTO
import com.zera.android.model.dto.telephone.UpdateTelephoneNumberRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TelephoneService {
    @GET("telephone/user")
    suspend fun getByUser(@Query("userId") userId: String): TelephoneResponseDTO

    @POST("telephone/user")
    suspend fun createForUser(@Body request: CreateTelephoneRequestDTO): TelephoneResponseDTO

    @PATCH("telephone/{telephoneId}/number")
    suspend fun updateNumber(
        @Path("telephoneId") telephoneId: String,
        @Body request: UpdateTelephoneNumberRequestDTO,
    ): Response<Void>
}
