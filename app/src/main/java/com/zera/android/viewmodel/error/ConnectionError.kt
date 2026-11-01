package com.zera.android.viewmodel.error

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ConnectionErrorState(
    val shouldExitApp: Boolean = false,
)

/**
 * @param fromBoot `true` quando a tela veio da Splash: tentar de novo refaz o boot. `false` quando
 *   veio do uso: tentar de novo volta para a tela anterior, que mantém o que já estava preenchido.
 */
class ConnectionErrorViewModel(
    private val fromBoot: Boolean = false,
) : ZeraViewModel() {
    private val _state = mutableStateOf(ConnectionErrorState())
    val state = _state

    fun onRetryClick() {
        if (fromBoot) {
            ZeraNavigator.pushAndPopAll(Route.Splash)
        } else {
            ZeraNavigator.goBack()
        }
    }

    /** Sinaliza para a tela fechar a Activity, já que o ViewModel não conhece a View. */
    fun onExitAppClick() {
        _state.value = _state.value.copy(shouldExitApp = true)
    }
}
