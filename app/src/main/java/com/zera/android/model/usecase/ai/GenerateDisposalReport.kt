package com.zera.android.model.usecase.ai

import com.zera.android.model.dto.ai.CreateDisposalReportRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AiClient
import retrofit2.HttpException

class GenerateDisposalReport {
    suspend fun execute(disposalId: String): String {
        val userId = SqliteManager.getUserId()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Usuário não autenticado.")
        if (SqliteManager.getAccessToken().isNullOrBlank()) {
            throw IllegalStateException("Entre de novo para gerar o relatório.")
        }
        val id = disposalId.trim()
        require(id.isNotEmpty()) { "Descarte sem identificador." }
        try {
            return AiClient.reportsService.createReport(
                CreateDisposalReportRequestDTO(userId = userId, disposalId = id),
            ).reportUrl
        } catch (error: HttpException) {
            val message = failureMessage(error.code())
            if (message == null) throw error
            throw IllegalStateException(message)
        }
    }

    companion object {
        internal fun failureMessage(status: Int): String? = when (status) {
            403 -> "A unidade deste usuário não pode gerar o relatório."
            else -> null
        }
    }
}
