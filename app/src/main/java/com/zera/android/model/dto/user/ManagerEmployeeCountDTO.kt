package com.zera.android.model.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class ManagerEmployeeCountDTO(
    val managerId: String,
    val count: Int,
)
