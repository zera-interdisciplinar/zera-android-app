package com.zera.android.view.navigation

import com.zera.android.view.transition.ScreenAnimation

sealed interface NavCommand {
    data class Navigate(
        val route: Route,
        val animation: ScreenAnimation = ScreenAnimation.Fade,
    ) : NavCommand

    data class PushAndPop(
        val route: Route,
        val popCount: Int = 1,
        val animation: ScreenAnimation = ScreenAnimation.Fade,
    ) : NavCommand

    data class PushAndPopAll(
        val route: Route,
        val animation: ScreenAnimation = ScreenAnimation.Fade,
    ) : NavCommand

    data object GoBack : NavCommand
}
