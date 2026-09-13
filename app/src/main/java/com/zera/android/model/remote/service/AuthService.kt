package com.zera.android.model.remote.service

import com.zera.android.model.entity.auth.SingInRequestDTO
import com.zera.android.model.entity.auth.SingInResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    // sing in call
    @POST("auth/login")
    suspend fun signIn(@Body signInRequest: SingInRequestDTO): SingInResponseDTO

    // sing up call
    @POST("invitations/redeem")
    suspend fun signUp(@Body signUpRequest: SingInRequestDTO): SingInResponseDTO
}
