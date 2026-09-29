package com.zera.android.model.dto.invitation

import kotlinx.serialization.Serializable

@Serializable
data class PendingInvitationDTO(
    val code: String,
    val inviteeName: String,
    val expiresAt: String,
)
