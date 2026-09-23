package com.zera.android.model.usecase.auth

import com.zera.android.model.entity.invitation.RedeemRequestDTO
import com.zera.android.model.entity.invitation.RedeemResponseDTO
import com.zera.android.model.remote.client.ApiClient

class InvitationUseCase {
    suspend fun execute(
        token: String,
        name: String,
        email: String,
        password: String,
    ): RedeemResponseDTO {
        val redeemRequest = RedeemRequestDTO(token, name, email, password)
        return ApiClient.invitationService.redeem(redeemRequest)
    }
}
