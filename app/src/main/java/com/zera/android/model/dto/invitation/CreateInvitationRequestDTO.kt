package com.zera.android.model.dto.invitation

import kotlinx.serialization.Serializable

@Serializable
data class CreateInvitationRequestDTO(
    val managerId: String,
    val inviteeName: String,
)
