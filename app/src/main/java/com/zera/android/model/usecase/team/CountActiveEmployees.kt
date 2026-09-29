package com.zera.android.model.usecase.team

import com.zera.android.model.dto.user.ManagerEmployeeCountDTO
import com.zera.android.model.local.SharedPreferencesManager
import com.zera.android.model.remote.client.ApiClient

class CountActiveEmployees {
    suspend fun execute(): Int {
        val managerId = SharedPreferencesManager.getUserId()
            ?: error("Não há usuário logado")
        val counts = ApiClient.usersService.countByManager()
        return countFor(managerId, counts)
    }

    companion object {
        fun countFor(managerId: String, counts: List<ManagerEmployeeCountDTO>): Int =
            counts.firstOrNull { it.managerId == managerId }?.count ?: 0
    }
}
