package com.zera.android.view.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object ZeraNavigator {
    private val _commands = Channel<NavCommand>(Channel.BUFFERED)
    val commands: Flow<NavCommand> = _commands.receiveAsFlow()

    fun navigateTo(route: Route){
        _commands.trySend(NavCommand.Navigate(route))
    }

    fun goBack(){
        _commands.trySend(NavCommand.GoBack)
    }
}