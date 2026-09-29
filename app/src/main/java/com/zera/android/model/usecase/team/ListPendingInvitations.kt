package com.zera.android.model.usecase.team

import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient

class ListPendingInvitations {
    suspend fun execute(): List<PendingInvite> {
        val managerId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        return ApiClient.invitationService.listPending(managerId).map { invite ->
            PendingInvite(
                code = invite.code,
                inviteeName = invite.inviteeName,
                expiresAt = invite.expiresAt,
            )
        }
    }
}
