package com.zera.android.model.dto.invitation

import kotlinx.serialization.Serializable

@Serializable
data class CreateInvitationResponseDTO(
    val id: String,
    val code: String,
    val managerId: String,
    val unitId: String,
    val inviteeName: String,
    val expiresAt: String,
)
