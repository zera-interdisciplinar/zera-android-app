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
    data class ItemApproved(
        val itemId: String,
        val itemName: String,
        val itemSubtitle: String,
    ) : Route

    @Serializable
    data object Indexes : Route

    @Serializable
    data object Itens : Route

    @Serializable
    data object Recycling : Route

    @Serializable
    data object ItensSelection : Route

    @Serializable
    data object RecyclingResume : Route

    @Serializable
    data object SchedulingSuccess : Route

    @Serializable
    data object ItensResume : Route

    @Serializable
    data object Models : Route

    @Serializable
    data object ModelCreation : Route

    @Serializable
    data class ModelCreationSuccess(
        val modelName: String,
        val material: String,
        val brand: String,
        val notes: String,
    ) : Route

    // ROTAS DE OPERARIO
    @Serializable
    data object EmployeeHome : Route

    @Serializable
    data object Scan : Route

    @Serializable
    data object EmployeeItems : Route

    // ROTAS COMPARTILHADAS
    @Serializable
    data object Profile : Route
}