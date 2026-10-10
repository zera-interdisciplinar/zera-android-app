package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class PagedDisposalSchedulesDTO(
    val content: List<DisposalScheduleSummaryDTO> = emptyList(),
    val page: Int = 0,
    val size: Int = 20,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

@Serializable
data class DisposalScheduleSummaryDTO(
    val id: String,
    val scheduledAt: String,
    val placeName: String = "",
    val itemCount: Int = 0,
)

@Serializable
data class DisposalScheduleDetailDTO(
    val id: String,
    val scheduledAt: String,
    val placeId: String = "",
    val placeName: String = "",
    val items: List<ScheduledItemDTO> = emptyList(),
    val contact: ScheduleContactDTO? = null,
)

@Serializable
data class ScheduledItemDTO(
    val itemId: String,
    val displayCode: String = "",
    val name: String = "",
)

@Serializable
data class ScheduleContactDTO(
    val email: String? = null,
    val phone: String? = null,
)
