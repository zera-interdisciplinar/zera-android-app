package com.zera.android.model.dto.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisposalReportResponseDTO(
    @SerialName("report_url") val reportUrl: String,
)
