package com.zera.android.model.remote.client

import com.zera.android.model.dto.auth.SingInResponseDTO
import com.zera.android.model.usecase.auth.RefreshOutcome
import com.zera.android.model.usecase.auth.RestoreChoice
import com.zera.android.model.usecase.auth.chooseRestore
import com.zera.android.model.usecase.auth.refreshSession
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response as RetrofitResponse
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TokenRefreshAuthenticatorTest {
    @Test
    fun retriesOnceWithTheNewAccessToken() {
        var calls = 0
        val authenticator = authenticator(
            refresh = {
                calls++
                RefreshOutcome.Refreshed("new-token")
            },
        )

        val retry = authenticator.authenticate(null, unauthorized("Bearer old"))

        assertEquals("Bearer new-token", retry?.header("Authorization"))
        assertEquals(1, calls)
    }

    @Test
    fun second401DoesNotRefreshAndExpiresTheSession() {
        var calls = 0
        var expired = 0
        val authenticator = authenticator(
            refresh = {
                calls++
                RefreshOutcome.Refreshed("new-token")
            },
            onSessionExpired = { expired++ },
        )
        val first = unauthorized("Bearer old")

        val retry = authenticator.authenticate(null, unauthorized("Bearer new", prior = first))

        assertNull(retry)
        assertEquals(0, calls)
        assertEquals(1, expired)
    }

    @Test
    fun missingBearerDoesNotRefresh() {
        var calls = 0
        val authenticator = authenticator(
            refresh = {
                calls++
                RefreshOutcome.Refreshed("new-token")
            },
        )
        val request = Request.Builder().url("https://adm.example.com/auth/login").build()

        val retry = authenticator.authenticate(null, response(request))

        assertNull(retry)
        assertEquals(0, calls)
    }

    @Test
    fun loginAndRefreshPathsDoNotRefresh() {
        var calls = 0
        val authenticator = authenticator(
            refresh = {
                calls++
                RefreshOutcome.Refreshed("new-token")
            },
        )

        assertNull(authenticator.authenticate(null, unauthorized("Bearer old", path = "auth/login")))
        assertNull(authenticator.authenticate(null, unauthorized("Bearer old", path = "auth/refresh")))
        assertEquals(0, calls)
    }

    @Test
    fun unavailableRefreshKeepsTheSession() {
        var expired = 0
        val authenticator = authenticator(
            refresh = { RefreshOutcome.Unavailable },
            onSessionExpired = { expired++ },
        )

        assertNull(authenticator.authenticate(null, unauthorized("Bearer old")))
        assertEquals(0, expired)
    }

    @Test
    fun parallel401sShareOneRefreshAndExpireOnce() {
        val calls = AtomicInteger(0)
        val expired = AtomicInteger(0)
        lateinit var gate: TokenRefreshAuthenticator
        gate = authenticator(
            refresh = {
                calls.incrementAndGet()
                while (gate.arrivals.get() < 2) {
                    Thread.sleep(1)
                }
                RefreshOutcome.Unauthorized
            },
            onSessionExpired = { expired.incrementAndGet() },
        )
        val pool = Executors.newFixedThreadPool(2)
        try {
            val first = pool.submit<okhttp3.Request?> {
                gate.authenticate(null, unauthorized("Bearer a"))
            }
            val second = pool.submit<okhttp3.Request?> {
                gate.authenticate(null, unauthorized("Bearer b"))
            }
            assertNull(first.get(2, TimeUnit.SECONDS))
            assertNull(second.get(2, TimeUnit.SECONDS))
            assertEquals(1, calls.get())
            assertEquals(1, expired.get())
        } finally {
            pool.shutdownNow()
        }
    }

    @Test
    fun blankRefreshTokenDoesNotCallTheApi() = runBlocking {
        var calls = 0
        val outcome = refreshSession(
            refreshToken = "  ",
            call = {
                calls++
                error("não deveria chamar")
            },
            save = { _, _ -> error("não deveria gravar") },
        )

        assertEquals(RefreshOutcome.Unavailable, outcome)
        assertEquals(0, calls)
    }

    @Test
    fun refreshSessionSavesTheNewPair() = runBlocking {
        val saved = mutableListOf<Pair<String, String>>()
        val outcome = refreshSession(
            refreshToken = "old-refresh",
            call = {
                SingInResponseDTO(
                    userId = "user-1",
                    accessToken = "new-access",
                    refreshToken = "new-refresh",
                    tokenType = "Bearer",
                    expiresIn = 3600,
                )
            },
            save = { access, refresh -> saved += access to refresh },
        )

        assertEquals(RefreshOutcome.Refreshed("new-access"), outcome)
        assertEquals(listOf("new-access" to "new-refresh"), saved)
    }

    @Test
    fun refreshSessionAuthFailureDoesNotSave() = runBlocking {
        var saved = 0
        val outcome = refreshSession(
            refreshToken = "old-refresh",
            call = { throw unauthorizedHttp() },
            save = { _, _ -> saved++ },
        )

        assertEquals(RefreshOutcome.Unauthorized, outcome)
        assertEquals(0, saved)
    }

    @Test
    fun restorePrefersRefreshTokenOverSavedPassword() {
        assertEquals(
            RestoreChoice.Refresh,
            chooseRestore(refreshToken = "refresh", email = "a@b.com", password = "secret"),
        )
        assertEquals(
            RestoreChoice.Password,
            chooseRestore(refreshToken = null, email = "a@b.com", password = "secret"),
        )
        assertEquals(
            RestoreChoice.Missing,
            chooseRestore(refreshToken = "  ", email = null, password = null),
        )
    }

    @Test
    fun publicAuthRoutesDoNotSendTheAccessToken() {
        assertEquals(false, shouldSendAccessToken("/qa/administrative/api/v1/auth/login"))
        assertEquals(false, shouldSendAccessToken("/api/v1/auth/refresh"))
        assertEquals(false, shouldSendAccessToken("/api/v1/auth/logout"))
        assertEquals(false, shouldSendAccessToken("/api/v1/invitations/redeem"))
        assertEquals(true, shouldSendAccessToken("/api/v1/users/1"))
    }

    private fun authenticator(
        refresh: suspend () -> RefreshOutcome,
        onSessionExpired: () -> Unit = { error("sessão não deveria expirar") },
    ) = TokenRefreshAuthenticator(refresh, onSessionExpired)

    private fun unauthorized(
        bearer: String,
        path: String = "users/1",
        prior: Response? = null,
    ): Response {
        val request = Request.Builder()
            .url("https://adm.example.com/$path")
            .header("Authorization", bearer)
            .build()
        return response(request, prior)
    }

    private fun response(request: Request, prior: Response? = null): Response {
        val builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("{}".toResponseBody())
        if (prior != null) {
            builder.priorResponse(prior)
        }
        return builder.build()
    }

    private fun unauthorizedHttp(): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(RetrofitResponse.error<String>(401, body))
    }
}
