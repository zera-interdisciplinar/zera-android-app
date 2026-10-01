package com.zera.android.model.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEmailRequestDTO(
    val email: String,
)
