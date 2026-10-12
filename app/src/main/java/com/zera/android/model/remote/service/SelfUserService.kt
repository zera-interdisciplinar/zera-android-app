package com.zera.android.model.remote.service

import com.zera.android.model.dto.notification.DeviceTokenRequestDTO
import com.zera.android.model.dto.user.RenameUserRequestDTO
import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.dto.user.UpdateEmailRequestDTO
import com.zera.android.model.dto.user.UpdateImageRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
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

    @PATCH("users/{id}/image")
    suspend fun updateImage(
        @Path("id") id: String,
        @Body body: UpdateImageRequestDTO,
    ): Response<Void>

    @POST("users/me/device-tokens")
    suspend fun registerDeviceToken(@Body body: DeviceTokenRequestDTO): Response<Unit>

    @HTTP(method = "DELETE", path = "users/me/device-tokens", hasBody = true)
    suspend fun deleteDeviceToken(@Body body: DeviceTokenRequestDTO): Response<Unit>
}