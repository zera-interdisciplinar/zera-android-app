package com.zera.android.model.usecase.telephone

import com.zera.android.model.dto.telephone.CreateTelephoneRequestDTO
import com.zera.android.model.dto.telephone.TelephoneResponseDTO
import com.zera.android.model.dto.telephone.UpdateTelephoneNumberRequestDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.ApiClient
import retrofit2.HttpException

class SaveTelephone {
    suspend fun execute(telephoneId: String?, number: String): TelephoneResponseDTO {
        val userId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        return if (telephoneId.isNullOrBlank()) {
            ApiClient.telephoneService.createForUser(
                CreateTelephoneRequestDTO(userId = userId, number = number),
            )
        } else {
            val response = ApiClient.telephoneService.updateNumber(
                telephoneId,
                UpdateTelephoneNumberRequestDTO(number),
            )
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            TelephoneResponseDTO(
                telephoneId = telephoneId,
                number = number.filter { it.isDigit() },
                userId = userId,
            )
        }
    }
}
