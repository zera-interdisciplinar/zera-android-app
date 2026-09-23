package com.zera.android.model.remote.service

import com.zera.android.model.entity.auth.SingInRequestDTO
import com.zera.android.model.entity.auth.SingInResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    suspend fun signIn(@Body signInRequest: SingInRequestDTO): SingInResponseDTO
}
