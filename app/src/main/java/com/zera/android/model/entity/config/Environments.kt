package com.zera.android.model.entity.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Environments(
    @SerialName("ms-adm-core-url")
    val admCoreApiUrl: String,
)
