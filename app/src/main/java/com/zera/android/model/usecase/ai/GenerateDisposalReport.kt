package com.zera.android.model.usecase.ai

import com.zera.android.model.dto.ai.CreateDisposalReportRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AiClient

class GenerateDisposalReport {
    suspend fun execute(disposalId: String): String {
        val userId = SqliteManager.getUserId()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Usuário não autenticado.")
        val id = disposalId.trim()
        require(id.isNotEmpty()) { "Descarte sem identificador." }
        return AiClient.reportsService.createReport(
            CreateDisposalReportRequestDTO(userId = userId, disposalId = id),
        ).reportUrl
    }
}
