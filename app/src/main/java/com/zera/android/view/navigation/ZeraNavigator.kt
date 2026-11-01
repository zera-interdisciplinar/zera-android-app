package com.zera.android.view.navigation

import com.zera.android.view.transition.ScreenAnimation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

object ZeraNavigator {
    private val _commands = Channel<NavCommand>(Channel.BUFFERED)
    val commands: Flow<NavCommand> = _commands.receiveAsFlow()

    private val _navigationEnabled = MutableStateFlow(true)
    val navigationEnabled: StateFlow<Boolean> = _navigationEnabled.asStateFlow()

    fun isNavigationEnabled(): Boolean = _navigationEnabled.value

    /**
     * Bloqueia ou habilita globalmente os comandos de navegação.
     *
     * Enquanto [enabled] for `false`, `push`, `pushAndPop`, `pushAndPopAll` e `goBack`
     * são ignorados. Chama este método com `true` antes de retomar uma navegação
     * explícita ou ao encerrar o carregamento que exigiu o bloqueio.
     */
    fun navigationStatus(enabled: Boolean) {
        _navigationEnabled.value = enabled
    }

    fun push(route: Route, animation: ScreenAnimation = ScreenAnimation.Fade) {
        if (isNavigationEnabled()) {
            _commands.trySend(NavCommand.Navigate(route, animation))
        }
    }

    fun goBack() {
        if (isNavigationEnabled()) {
            _commands.trySend(NavCommand.GoBack)
        }
    }

    /**
     * Navega para [route] e remove as [popCount] telas do topo da pilha (a atual e,
     * opcionalmente, as anteriores a ela), impedindo que o usuário volte para elas.
     * Ver [NavCommand.PushAndPop].
     */
    fun pushAndPop(
        route: Route,
        popCount: Int = 1,
        animation: ScreenAnimation = ScreenAnimation.Fade,
    ) {
        if (isNavigationEnabled()) {
            _commands.trySend(NavCommand.PushAndPop(route, popCount, animation))
        }
    }

    /**
     * Navega para [route] e esvazia a pilha (Splash, Welcome, SignIn, SignUp, etc.).
     */
    fun pushAndPopAll(route: Route, animation: ScreenAnimation = ScreenAnimation.Fade) {
        if (isNavigationEnabled()) {
            _commands.trySend(NavCommand.PushAndPopAll(route, animation))
        }
    }
}
