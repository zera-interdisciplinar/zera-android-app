package com.zera.android.model.usecase.auth

import com.zera.android.model.entity.user.SelfUserResponseDTO
import com.zera.android.model.local.SharedPreferencesManager

class RestoreSession {
    private val signIn = SingIn()

    fun hasSavedLogin(): Boolean = SharedPreferencesManager.hasSavedLogin()

    suspend fun execute(): SelfUserResponseDTO {
        val email = SharedPreferencesManager.getEmail()
            ?: error("Não há login salvo")
        val password = SharedPreferencesManager.getPassword()
            ?: error("Não há login salvo")
        return signIn.execute(email, password)
    }

    fun clearSession() {
        SharedPreferencesManager.clearSession()
    }
}
