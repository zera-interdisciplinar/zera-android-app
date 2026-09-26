package com.zera.android.model.dto.invitation

import kotlinx.serialization.Serializable

@Serializable
data class RedeemResponseDTO(
    val userId: String,
    val name: String,
    val email: String,
    val role: String, // MANAGER, EMPLOYEE
    val managerId: String
)
