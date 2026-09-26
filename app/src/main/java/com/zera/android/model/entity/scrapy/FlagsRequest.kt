package com.zera.android.model.entity.scrapy

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FlagsRequest(
    val attrs: Map<String, JsonElement> = emptyMap(),
)
