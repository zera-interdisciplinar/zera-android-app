package com.zera.android.model.remote.service

import com.zera.android.model.dto.auth.RefreshRequestDTO
import com.zera.android.model.dto.auth.SingInRequestDTO
import com.zera.android.model.dto.auth.SingInResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun signIn(@Body signInRequest: SingInRequestDTO): SingInResponseDTO

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDTO): SingInResponseDTO
}
