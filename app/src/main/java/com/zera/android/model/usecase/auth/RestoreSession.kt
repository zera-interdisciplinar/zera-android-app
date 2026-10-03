package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.local.SqliteManager

class RestoreSession {
    private val signIn = SingIn()

    fun hasSavedLogin(): Boolean = SqliteManager.hasSavedLogin()

    suspend fun execute(): SelfUserResponseDTO {
        val email = SqliteManager.getEmail()
            ?: error("Não há login salvo")
        val password = SqliteManager.getPassword()
            ?: error("Não há login salvo")
        return signIn.execute(email, password)
    }

    fun clearSession() {
        signIn.clearSession()
    }
}
