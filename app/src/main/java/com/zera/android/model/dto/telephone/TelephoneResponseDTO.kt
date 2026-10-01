package com.zera.android.model.dto.telephone

import kotlinx.serialization.Serializable

@Serializable
data class TelephoneResponseDTO(
    val telephoneId: String,
    val number: String,
    val userId: String? = null,
    val organizationId: String? = null,
    val unitId: String? = null,
    val recyclingBusinessId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
) {
    fun belongsToUser(expectedUserId: String): Boolean {
        if (userId != expectedUserId) return false
        return organizationId.isNullOrBlank() &&
            unitId.isNullOrBlank() &&
            recyclingBusinessId.isNullOrBlank()
    }
}
