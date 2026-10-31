package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.auth.PasswordResetRequestDTO
import com.zera.android.model.remote.client.AdmCoreClient

class RequestPasswordReset {
    suspend fun execute(email: String) {
        AdmCoreClient.authService.requestPasswordReset(PasswordResetRequestDTO(email.trim()))
    }
}
