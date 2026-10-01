package com.zera.android.model.remote.service

import com.zera.android.model.dto.user.RenameUserRequestDTO
import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.dto.user.UpdateEmailRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SelfUserService {
    @GET("users/{userId}")
    suspend fun getSelfUser(@Path("userId") userId: String): SelfUserResponseDTO

    @PATCH("users/{id}/rename")
    suspend fun rename(
        @Path("id") id: String,
        @Body body: RenameUserRequestDTO,
    ): Response<Void>

    @PATCH("users/{id}/email")
    suspend fun updateEmail(
        @Path("id") id: String,
        @Body body: UpdateEmailRequestDTO,
    ): Response<Void>
}