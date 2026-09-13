package com.zera.android.model.remote.service
import com.zera.android.model.entity.user.SelfUserResponseDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface SelfUserService {
    @GET("users/{userId}")
    suspend fun getSelfUser(@Path("userId") userId: String): SelfUserResponseDTO
}