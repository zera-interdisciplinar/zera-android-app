package com.zera.android.model.dto.recycling

import kotlinx.serialization.Serializable

@Serializable
data class RecyclingBusinessResponseDTO(
    val id: String,
    val name: String,
    val cnpj: String? = null,
    val email: String? = null,
)
