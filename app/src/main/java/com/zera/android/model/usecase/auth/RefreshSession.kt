package com.zera.android.model.usecase.auth

import com.zera.android.model.dto.auth.RefreshRequestDTO
import com.zera.android.model.dto.auth.SingInResponseDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.ApiClient
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

sealed class RefreshOutcome {
    data class Refreshed(val accessToken: String) : RefreshOutcome()
    data object Unauthorized : RefreshOutcome()
    data object Unavailable : RefreshOutcome()
}

class RefreshSession {
    suspend fun execute(): RefreshOutcome = refreshSession(
        refreshToken = SqliteManager.getRefreshToken(),
        call = { token -> ApiClient.refreshAuthService.refresh(RefreshRequestDTO(token)) },
        save = SqliteManager::updateTokens,
    )
}

internal suspend fun refreshSession(
    refreshToken: String?,
    call: suspend (String) -> SingInResponseDTO,
    save: (accessToken: String, refreshToken: String) -> Unit,
): RefreshOutcome {
    if (refreshToken.isNullOrBlank()) return RefreshOutcome.Unavailable
    return try {
        val response = call(refreshToken)
        save(response.accessToken, response.refreshToken)
        RefreshOutcome.Refreshed(response.accessToken)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        if (error is HttpException && error.code() in AUTH_FAILURE_CODES) {
            RefreshOutcome.Unauthorized
        } else {
            RefreshOutcome.Unavailable
        }
    }
}

private val AUTH_FAILURE_CODES = listOf(401, 403)
