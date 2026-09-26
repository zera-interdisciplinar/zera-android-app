package com.zera.android.model.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class SingInRequestDTO(
    val email: String,
    val password: String
)
