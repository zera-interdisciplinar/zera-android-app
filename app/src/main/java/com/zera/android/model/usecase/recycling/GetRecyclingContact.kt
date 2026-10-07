package com.zera.android.model.usecase.recycling

import com.zera.android.model.remote.client.ApiClient
import retrofit2.HttpException

data class RecyclingContact(
    val email: String = "",
    val phone: String = "",
)

class GetRecyclingContact {
    suspend fun execute(recyclingBusinessId: String, email: String): RecyclingContact {
        val id = recyclingBusinessId.trim()
        if (id.isEmpty()) return RecyclingContact()
        val phone = try {
            ApiClient.telephoneService.getByRecyclingBusiness(id).number
        } catch (error: HttpException) {
            if (error.code() == 404) "" else throw error
        }
        return RecyclingContact(
            email = email.trim(),
            phone = phone.orEmpty(),
        )
    }
}
