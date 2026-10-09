package com.zera.android.model.usecase.team

import com.zera.android.model.entity.team.PendingInvite
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient

class ListPendingInvitations {
    suspend fun execute(): List<PendingInvite> {
        val managerId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        return AdmCoreClient.invitationService.listPending(managerId).map { invite ->
            PendingInvite(
                code = invite.code,
                inviteeName = invite.inviteeName,
                expiresAt = invite.expiresAt,
            )
        }
    }
}
