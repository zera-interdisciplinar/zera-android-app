package com.zera.android.model.dto.invitation

import kotlinx.serialization.Serializable

@Serializable
data class RedeemRequestDTO(
    val code: String,
    val name: String,
    val email: String,
    val rawPassword: String
)
