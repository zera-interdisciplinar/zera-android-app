package com.zera.android.model.entity.auth

import kotlinx.serialization.Serializable

@Serializable
data class SingInRequestDTO(
    val email: String,
    val password: String
)