package com.zera.android.model.dto.notification

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequestDTO(
    val token: String,
    val platform: String? = null,
)
