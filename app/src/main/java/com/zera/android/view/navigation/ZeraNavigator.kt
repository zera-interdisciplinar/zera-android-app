package com.zera.android.view.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object ZeraNavigator {
    private val _commands = Channel<NavCommand>(Channel.BUFFERED)
    val commands: Flow<NavCommand> = _commands.receiveAsFlow()

    fun push(route: Route) {
        _commands.trySend(NavCommand.Navigate(route))
    }

    fun goBack() {
        _commands.trySend(NavCommand.GoBack)
    }

    /**
     * Navega para [route] e remove a tela atual da pilha, impedindo que o usuário
     * volte para ela. Ver [NavCommand.PushAndPop].
     */
    fun pushAndPop(route: Route) {
        _commands.trySend(NavCommand.PushAndPop(route))
    }

    /**
     * Navega para [route] e esvazia a pilha (Splash, Welcome, Login, Register, etc.).
     */
    fun pushAndClear(route: Route) {
        _commands.trySend(NavCommand.PushAndClear(route))
    }
}
