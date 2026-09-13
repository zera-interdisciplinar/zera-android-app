package com.zera.android.model.entity.auth

import kotlinx.serialization.Serializable

@Serializable
data class SingInResponseDTO(
    var userId: String,
    var accessToken: String,
    var refreshToken: String,
    var tokenType: String,
    var expiresIn: Long
)