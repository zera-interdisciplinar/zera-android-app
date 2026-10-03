package com.zera.android.model.usecase.telephone

import com.zera.android.model.dto.telephone.TelephoneResponseDTO
import com.zera.android.model.remote.client.ApiClient
import retrofit2.HttpException

class GetTelephone {
    suspend fun execute(userId: String): TelephoneResponseDTO? {
        val telephone = try {
            ApiClient.telephoneService.getByUser(userId)
        } catch (e: HttpException) {
            if (e.code() == 404) return null else throw e
        }
        return telephone.takeIf { it.belongsToUser(userId) }
    }
}
