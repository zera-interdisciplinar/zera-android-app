package com.zera.android.viewmodel.error

import androidx.compose.runtime.mutableStateOf
import com.zera.android.view.navigation.Route
import com.zera.android.view.navigation.ZeraNavigator
import com.zera.android.viewmodel.ZeraViewModel

data class ConnectionErrorState(
    val shouldExitApp: Boolean = false,
)

class ConnectionErrorViewModel : ZeraViewModel() {
    private val _state = mutableStateOf(ConnectionErrorState())
    val state = _state

    /** Reinicia o app pela Splash, que refaz o boot e restaura a sessão. */
    fun onRetryClick() {
        ZeraNavigator.pushAndPopAll(Route.Splash)
    }

    /** Sinaliza para a tela fechar a Activity, já que o ViewModel não conhece a View. */
    fun onExitAppClick() {
        _state.value = _state.value.copy(shouldExitApp = true)
    }
}
