package com.zera.android.view.navigation

import kotlinx.serialization.Serializable

sealed interface Route{

    @Serializable
    data object Splash : Route

    @Serializable
    data object Welcome : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object SignUp : Route

    // ROTAS DE GESTOR
    @Serializable
    data object ManagerHome : Route

    @Serializable
    data class ItemDetails(val itemId: String) : Route

    @Serializable
    data object Employees : Route

    @Serializable
    data object ItemApproved : Route

    @Serializable
    data object Indexes : Route

    @Serializable
    data object Itens : Route

    // ROTAS DE OPERARIO
    @Serializable
    data object EmployeeHome : Route

    // ROTAS COMPARTILHADAS
    @Serializable
    data object Profile : Route
}