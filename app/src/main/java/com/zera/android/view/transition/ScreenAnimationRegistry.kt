package com.zera.android.view.transition

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

class ScreenAnimationRegistry {
    private val animations = mutableMapOf<String, ScreenAnimation>()
    private var pending: ScreenAnimation? = null

    fun prepare(animation: ScreenAnimation, navController: NavController) {
        val liveIds = navController.currentBackStack.value.map { it.id }.toSet()
        animations.keys.retainAll(liveIds)
        pending = animation
    }

    fun of(entry: NavBackStackEntry): ScreenAnimation =
        animations.getOrPut(entry.id) {
            (pending ?: ScreenAnimation.Fade).also { pending = null }
        }
}
