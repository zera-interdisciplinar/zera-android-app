package com.zera.android.model.usecase.ai

import com.zera.android.model.remote.client.AiClient
import retrofit2.HttpException

class GetDisposalReport {
    suspend fun execute(disposalId: String): String? {
        val id = disposalId.trim()
        if (id.isEmpty()) return null
        return try {
            AiClient.reportsService.getReport(id).reportUrl.takeIf { it.isNotBlank() }
        } catch (error: HttpException) {
            if (error.code() == 404) null else throw error
        }
    }
}
