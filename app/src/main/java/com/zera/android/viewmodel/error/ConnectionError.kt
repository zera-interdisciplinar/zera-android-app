package com.zera.android.viewmodel.error

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.zera.android.model.remote.client.ConnectionLoss
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class ConnectionErrorState(
    val isChecking: Boolean = false,
    val shouldExitApp: Boolean = false,
)

/**
 * @param fromBoot `true` quando a tela veio da Splash: tentar de novo refaz o boot. `false` quando
 *   veio do uso: tentar de novo volta para a tela anterior, que mantém o que já estava preenchido.
 * @param isOnline checa a conexão antes de sair da tela (ver `NetworkStatus.isOnline`).
 */
class ConnectionErrorViewModel(
    private val fromBoot: Boolean = false,
    private val isOnline: () -> Boolean = { true },
) : ZeraViewModel() {
    private val _state = mutableStateOf(ConnectionErrorState())
    val state = _state

    /** Só sai da tela se a conexão voltou; ainda offline, continua aqui. */
    fun onRetryClick() {
        if (_state.value.isChecking) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isChecking = true)
            // Mantém o loading visível: a checagem é instantânea e o toque pareceria ignorado.
            delay(MIN_CHECK_DURATION)
            val online = isOnline()
            _state.value = _state.value.copy(isChecking = false)
            if (!online) return@launch
            if (fromBoot) {
                ZeraNavigator.pushAndPopAll(Route.Splash)
            } else {
                ZeraNavigator.goBack()
            }
        }
    }

    /** Sinaliza para a tela fechar a Activity, já que o ViewModel não conhece a View. */
    fun onExitAppClick() {
        _state.value = _state.value.copy(shouldExitApp = true)
    }

    /** A tela saiu da pilha: o interceptor pode abrir outra na próxima falha de conexão. */
    override fun onCleared() {
        // A tela vinda da Splash não ocupou a trava (a Splash suprime o interceptor).
        if (!fromBoot) ConnectionLoss.gate.release()
    }

    companion object {
        private val MIN_CHECK_DURATION = 600.milliseconds
    }
}
