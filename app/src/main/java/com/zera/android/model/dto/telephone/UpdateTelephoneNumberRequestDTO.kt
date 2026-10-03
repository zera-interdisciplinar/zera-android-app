package com.zera.android.model.dto.telephone

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTelephoneNumberRequestDTO(
    val number: String,
)
