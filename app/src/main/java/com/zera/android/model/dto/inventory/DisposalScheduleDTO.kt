package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class PagedDisposalsDTO(
    val content: List<DisposalRecordDTO> = emptyList(),
    val page: Int = 0,
    val size: Int = 20,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
)

@Serializable
data class DisposalRecordDTO(
    val id: String,
    val destination: String = "",
    val placeId: String? = null,
    val placeName: String? = null,
    val disposedAt: String = "",
    val notes: String? = null,
    val itemIds: List<String> = emptyList(),
    val items: List<DisposedItemDTO> = emptyList(),
) {
    fun itemCount(): Int = if (items.isNotEmpty()) items.size else itemIds.size

    fun itemLabels(): List<String> = items.map { item ->
        item.name.ifBlank { item.displayCode }
    }.filter { it.isNotBlank() }
}

@Serializable
data class DisposedItemDTO(
    val id: String = "",
    val itemId: String = "",
    val displayCode: String = "",
    val name: String = "",
)
