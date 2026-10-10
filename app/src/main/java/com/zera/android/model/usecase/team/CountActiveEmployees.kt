package com.zera.android.model.usecase.team

import com.zera.android.model.dto.user.ManagerEmployeeCountDTO
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.AdmCoreClient

class CountActiveEmployees {
    suspend fun execute(): Int {
        val managerId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        val counts = AdmCoreClient.usersService.countByManager()
        return countFor(managerId, counts)
    }

    companion object {
        fun countFor(managerId: String, counts: List<ManagerEmployeeCountDTO>): Int =
            counts.firstOrNull { it.managerId == managerId }?.count ?: 0
    }
}
