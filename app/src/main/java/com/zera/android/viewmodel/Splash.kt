package com.zera.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.ConnectionLoss
import com.zera.android.model.remote.client.isConnectionError
import com.zera.android.model.usecase.auth.RestoreSession
import com.zera.android.model.usecase.config.Boot
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.view.navigation.consumePendingAlertDeepLink
import com.zera.android.view.navigation.consumePendingPasswordResetDeepLink
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.time.Duration.Companion.milliseconds


class SplashViewModel : ViewModel() {
    private val boot = Boot()
    private val restoreSession = RestoreSession()

    init {
        viewModelScope.launch {
            // A Splash mesma navega para a tela de conexão; o interceptor não abre outra.
            ConnectionLoss.gate.suppress(true)
            try {
                bootAndNavigate()
            } finally {
                ConnectionLoss.gate.suppress(false)
            }
        }
    }

    private suspend fun bootAndNavigate() = coroutineScope {
        val minSplash = async { delay(2_000.milliseconds) }
        try {
            boot.execute()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            minSplash.await()
            ZeraNavigator.pushAndPop(bootErrorRoute(e))
            return@coroutineScope
        }
        minSplash.await()
        navigateAfterBoot()
    }

    private suspend fun navigateAfterBoot() {
        if (consumePendingPasswordResetDeepLink()) {
            return
        }
        if (!restoreSession.hasSavedLogin()) {
            ZeraNavigator.pushAndPop(Route.Welcome)
            return
        }

        try {
            val selfUser = restoreSession.execute()
            val destination = homeRouteForRole(selfUser.role)
            if (destination == null) {
                restoreSession.clearSession()
                ZeraNavigator.pushAndPop(Route.Welcome)
                return
            }
            ZeraNavigator.pushAndPopAll(destination)
            consumePendingAlertDeepLink()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            // Sem conexão não é sessão inválida: mantém o login salvo para tentar de novo.
            if (error.isConnectionError()) {
                ZeraNavigator.pushAndPop(Route.ConnectionError(fromBoot = true))
                return
            }
            if (error is HttpException && error.code() in AUTH_FAILURE_CODES) {
                restoreSession.clearSession()
            }
            ZeraNavigator.pushAndPop(Route.Welcome)
        }
    }

    companion object {
        private val AUTH_FAILURE_CODES = listOf(401, 403)

        /**
         * Tela de erro para uma falha no boot: sem conexão → [Route.ConnectionError], que refaz o
         * boot ao tentar de novo; qualquer outra (5xx, configuração inválida) → [Route.FatalError].
         */
        internal fun bootErrorRoute(error: Throwable): Route =
            if (error.isConnectionError()) Route.ConnectionError(fromBoot = true) else Route.FatalError
    }
}
