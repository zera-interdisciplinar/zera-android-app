package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class DashboardHomeResponseDTO(
    val activeItems: Long,
    val activeItemsChangePercent: Double? = null,
    val occupancyPercent: Double? = null,
    val pendingApproval: Long = 0,
    val inMaintenance: Long = 0,
    val awaitingEvaluation: Long = 0,
    val recentItems: PagedItemsDTO = PagedItemsDTO(),
)

@Serializable
data class PagedItemsDTO(
    val content: List<ItemResponseDTO> = emptyList(),
)
