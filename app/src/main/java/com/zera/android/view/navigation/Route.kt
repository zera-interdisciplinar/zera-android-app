package com.zera.android.view.navigation

import kotlinx.serialization.Serializable

sealed interface Route{

    @Serializable
    data object Splash : Route
}