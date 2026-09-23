package com.zera.android.model.remote.service

import com.zera.android.model.entity.invitation.RedeemRequestDTO
import com.zera.android.model.entity.invitation.RedeemResponseDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface InvitationService {
    @POST("invitations/redeem")
    suspend fun redeem(@Body request: RedeemRequestDTO): RedeemResponseDTO
}
