package com.zera.android.model.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class PasswordResetConfirmDTO(
    val token: String,
    val newPassword: String,
)
