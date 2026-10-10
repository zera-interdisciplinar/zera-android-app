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
    data class ItensSelection(
        val placeId: String,
        val placeName: String,
        val placeAddress: String,
        val distanceMeters: Long,
        val isOpen: Boolean? = null,
        val description: String = "",
        val openingDays: List<String> = emptyList(),
        val openingHourLabels: List<String> = emptyList(),
        val recyclingBusinessId: String = "",
        val contactEmail: String = "",
    ) : Route

    @Serializable
    data class RecyclingResume(
        val placeId: String,
        val placeName: String,
        val placeAddress: String,
        val distanceMeters: Long,
        val itemIds: List<String>,
        val itemNames: List<String>,
        val isOpen: Boolean? = null,
        val description: String = "",
        val openingDays: List<String> = emptyList(),
        val openingHourLabels: List<String> = emptyList(),
        val recyclingBusinessId: String = "",
        val contactEmail: String = "",
    ) : Route

    @Serializable
    data class SchedulingSuccess(
        val recyclerName: String,
        val scheduledAt: String,
        val materials: String,
        val contactEmail: String,
        val contactPhone: String,
        val itemNames: List<String>,
        val disposalId: String,
    ) : Route

    @Serializable
    data object SchedulingDetails : Route

    @Serializable
    data class ItensResume(
        val itemIds: List<String>,
        val itemNames: List<String>,
    ) : Route

    @Serializable
    data object ScheduledDisposals : Route

    @Serializable
    data object Models : Route

    @Serializable
    data class ModelItems(
        val modelId: String,
        val modelName: String,
    ) : Route

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

    @Serializable
    data object WorkingCentral : Route

    @Serializable
    data class ManualRegister(val modelName: String? = null) : Route

    // ROTAS COMPARTILHADAS
    @Serializable
    data object Profile : Route
}