package com.zera.android.model.remote.service

import com.zera.android.model.dto.auth.PasswordResetConfirmDTO
import com.zera.android.model.dto.auth.PasswordResetRequestDTO
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

    // 202 sem corpo, exista a conta ou não (evita enumeração de e-mails).
    @POST("auth/password-reset/request")
    suspend fun requestPasswordReset(@Body body: PasswordResetRequestDTO)

    // 204 sem corpo; token inválido, usado ou expirado → 400.
    @POST("auth/password-reset/confirm")
    suspend fun confirmPasswordReset(@Body body: PasswordResetConfirmDTO)
}
