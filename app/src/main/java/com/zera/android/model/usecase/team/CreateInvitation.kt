package com.zera.android.model.usecase.team

import com.zera.android.model.dto.invitation.CreateInvitationRequestDTO
import com.zera.android.model.dto.invitation.CreateInvitationResponseDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient

class CreateInvitation {
    suspend fun execute(inviteeName: String): CreateInvitationResponseDTO {
        val managerId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        return ApiClient.invitationService.create(
            CreateInvitationRequestDTO(
                managerId = managerId,
                inviteeName = inviteeName.trim(),
            )
        )
    }
}
