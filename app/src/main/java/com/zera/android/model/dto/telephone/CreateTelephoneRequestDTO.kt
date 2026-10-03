package com.zera.android.model.dto.telephone

import kotlinx.serialization.Serializable

@Serializable
data class CreateTelephoneRequestDTO(
    val userId: String,
    val number: String,
)
