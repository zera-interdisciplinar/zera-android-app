package com.zera.android.model.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class SelfUserResponseDTO(
    var userId: String,
    var name: String,
    var email: String,
    var role: String, // MANAGER, EMPLOYEE
    var status: String,
    var unitId: String,
    var createdAt: String,
    var updatedAt: String,
    var managerId: String,
)