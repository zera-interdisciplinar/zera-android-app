package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.local.SqliteManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

enum class RestoreChoice {
    Refresh,
    Password,
    Missing,
}

fun chooseRestore(refreshToken: String?, email: String?, password: String?): RestoreChoice {
    if (!refreshToken.isNullOrBlank()) return RestoreChoice.Refresh
    if (!email.isNullOrBlank() && !password.isNullOrBlank()) return RestoreChoice.Password
    return RestoreChoice.Missing
}

class RestoreSession {
    private val signIn = SingIn()

    fun hasSavedLogin(): Boolean = SqliteManager.hasSavedLogin()

    suspend fun execute(): SelfUserResponseDTO {
        return when (
            chooseRestore(
                refreshToken = SqliteManager.getRefreshToken(),
                email = SqliteManager.getEmail(),
                password = SqliteManager.getPassword(),
            )
        ) {
            RestoreChoice.Refresh -> refresh()
            RestoreChoice.Password -> signIn.execute(
                email = SqliteManager.getEmail().orEmpty(),
                password = SqliteManager.getPassword().orEmpty(),
            )
            RestoreChoice.Missing -> error("Não há login salvo")
        }
    }

    fun clearSession() {
        signIn.clearSession()
    }

    private suspend fun refresh(): SelfUserResponseDTO {
        return when (val outcome = RefreshSession().execute()) {
            is RefreshOutcome.Refreshed -> {
                val userId = SqliteManager.getUserId() ?: error("Não há usuário logado")
                signIn.loadSignedInUser(userId)
            }
            RefreshOutcome.Unauthorized -> loginWithSavedPassword()
            RefreshOutcome.Unavailable -> throw IOException("Não foi possível renovar a sessão")
        }
    }

    private suspend fun loginWithSavedPassword(): SelfUserResponseDTO {
        val email = SqliteManager.getEmail()
        val password = SqliteManager.getPassword()
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            clearSession()
            throw unauthorized()
        }
        return signIn.execute(email, password)
    }

    private fun unauthorized(): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<String>(401, body))
    }
}
