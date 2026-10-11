package com.zera.android.model.dto.inventory

import kotlinx.serialization.Serializable

@Serializable
data class CreateItemRequestDTO(
    val name: String,
    val modelId: String,
    val barcode: String? = null,
    val serialNumber: String? = null,
    val condition: String? = null,
    val hasDamages: Boolean? = null,
    val damages: List<String>? = null,
    val notes: String? = null,
    val acquiredAt: String? = null,
    val manufacturingYear: Int? = null,
    val usageIntensity: Int? = null,
)
