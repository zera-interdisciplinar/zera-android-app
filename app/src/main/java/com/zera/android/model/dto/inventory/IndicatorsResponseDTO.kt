package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class IndicatorsResponseDTO(
    val recyclingRatePercent: Double,
    val recyclingRateChangePoints: Double? = null,
    val monthlyWeightKg: List<MonthlyWeightDTO> = emptyList(),
    val weightByMaterial: List<WeightByMaterialDTO> = emptyList(),
)

@Serializable
data class MonthlyWeightDTO(
    val month: String,
    val weightKg: Double,
)

@Serializable
data class WeightByMaterialDTO(
    val material: String,
    val percent: Double,
)
