package com.zera.android.model.remote.service

import com.zera.android.model.dto.invitation.CreateInvitationRequestDTO
import com.zera.android.model.dto.invitation.CreateInvitationResponseDTO
import com.zera.android.model.dto.invitation.PendingInvitationDTO
import com.zera.android.model.dto.invitation.RedeemRequestDTO
import com.zera.android.model.dto.invitation.RedeemResponseDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InvitationService {
    @GET("invitations/pending")
    suspend fun listPending(@Query("managerId") managerId: String): List<PendingInvitationDTO>

    @POST("invitations")
    suspend fun create(@Body request: CreateInvitationRequestDTO): CreateInvitationResponseDTO

    @POST("invitations/redeem")
    suspend fun redeem(@Body request: RedeemRequestDTO): RedeemResponseDTO
}

