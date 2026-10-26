package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.auth.PasswordResetConfirmDTO
import com.zera.android.model.remote.client.AdmCoreClient

class ConfirmPasswordReset {
    suspend fun execute(token: String, newPassword: String) {
        AdmCoreClient.authService.confirmPasswordReset(PasswordResetConfirmDTO(token, newPassword))
    }
}
