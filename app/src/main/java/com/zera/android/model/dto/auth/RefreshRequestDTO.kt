package com.zera.android.model.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDTO(
    val refreshToken: String,
)
