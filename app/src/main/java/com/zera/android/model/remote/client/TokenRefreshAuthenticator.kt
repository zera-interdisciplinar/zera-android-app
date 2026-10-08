package com.zera.android.model.remote.client

import com.zera.android.model.local.SqliteManager
import com.zera.android.model.usecase.auth.RefreshOutcome
import com.zera.android.model.usecase.auth.RefreshSession
import com.zera.android.model.usecase.auth.SingIn
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route as OkHttpRoute
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.io.IOException

class TokenRefreshAuthenticator(
    private val refresh: suspend () -> RefreshOutcome,
    private val onSessionExpired: () -> Unit,
) : Authenticator {
    private val mutex = Mutex()
    private var inFlight: CompletableDeferred<RefreshOutcome>? = null
    private val expired = AtomicBoolean(false)
    private val recovering = AtomicBoolean(false)
    internal val arrivals = AtomicInteger(0)

    override fun authenticate(route: OkHttpRoute?, response: Response): Request? {
        if (recovering.get()) return null
        val request = response.request
        if (request.header("Authorization").isNullOrBlank()) return null
        if (isCredentialRequest(request)) return null
        if (unauthorizedAttempts(response) >= 2) {
            expire()
            return null
        }

        val outcome = runBlocking { coalesce() }
        return when (outcome) {
            is RefreshOutcome.Refreshed -> {
                expired.set(false)
                request.newBuilder()
                    .header("Authorization", "Bearer ${outcome.accessToken}")
                    .build()
            }
            RefreshOutcome.Unauthorized -> {
                when (val recovered = loginAgain()) {
                    is Relogin.Token -> {
                        expired.set(false)
                        request.newBuilder()
                            .header("Authorization", "Bearer ${recovered.accessToken}")
                            .build()
                    }
                    Relogin.KeepSession -> null
                    Relogin.GiveUp -> {
                        expire()
                        null
                    }
                }
            }
            RefreshOutcome.Unavailable -> null
        }
    }

    private fun expire() {
        if (expired.compareAndSet(false, true)) onSessionExpired()
    }

    private fun loginAgain(): Relogin {
        if (!recovering.compareAndSet(false, true)) return Relogin.KeepSession
        return try {
            runBlocking {
                val email = SqliteManager.getEmail()
                val password = SqliteManager.getPassword()
                if (email.isNullOrBlank() || password.isNullOrBlank()) return@runBlocking Relogin.GiveUp
                SingIn().execute(email, password)
                val access = SqliteManager.getAccessToken()
                if (access.isNullOrBlank()) Relogin.GiveUp else Relogin.Token(access)
            }
        } catch (_: IOException) {
            Relogin.KeepSession
        } catch (_: Exception) {
            Relogin.GiveUp
        } finally {
            recovering.set(false)
        }
    }

    private suspend fun coalesce(): RefreshOutcome {
        val (deferred, leader) = mutex.withLock {
            arrivals.incrementAndGet()
            val existing = inFlight
            if (existing != null) {
                existing to false
            } else {
                val created = CompletableDeferred<RefreshOutcome>()
                inFlight = created
                created to true
            }
        }
        if (!leader) return deferred.await()

        val created = deferred as CompletableDeferred<RefreshOutcome>
        try {
            val outcome = try {
                refresh()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                RefreshOutcome.Unavailable
            }
            created.complete(outcome)
            return outcome
        } catch (error: CancellationException) {
            created.completeExceptionally(error)
            throw error
        } finally {
            mutex.withLock {
                if (inFlight === created) inFlight = null
            }
        }
    }

    private fun isCredentialRequest(request: Request): Boolean {
        val path = request.url.encodedPath.trimEnd('/')
        return path.endsWith("/auth/login") || path.endsWith("/auth/refresh")
    }

    private fun unauthorizedAttempts(response: Response): Int {
        var count = 0
        var current: Response? = response
        while (current != null) {
            if (current.code == 401) count++
            current = current.priorResponse
        }
        return count
    }
}

object SessionRefresh {
    val authenticator: Authenticator = TokenRefreshAuthenticator(
        refresh = { RefreshSession().execute() },
        onSessionExpired = {
            SingIn().clearSession()
            ZeraNavigator.pushAndPop(Route.Welcome)
        },
    )
}

private sealed class Relogin {
    data class Token(val accessToken: String) : Relogin()
    data object KeepSession : Relogin()
    data object GiveUp : Relogin()
}
