package com.zera.android.view.navigation

import kotlinx.serialization.Serializable

sealed interface Route{

    @Serializable
    data object Splash : Route

    @Serializable
    data object Welcome : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    // ROTAS DE GESTOR
    @Serializable
    data object ManagerHome : Route

    // ROTAS DE OPERARIO
    @Serializable
    data object EmployeeHome : Route
}