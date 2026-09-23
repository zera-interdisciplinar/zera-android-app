package com.zera.android.view.navigation

sealed interface NavCommand {
    data class Navigate(
        val route: Route
    ) : NavCommand

    data class PushAndPop(
        val route: Route
    ) : NavCommand

    data class PushAndPopAll(
        val route: Route
    ) : NavCommand

    data object GoBack : NavCommand
}
