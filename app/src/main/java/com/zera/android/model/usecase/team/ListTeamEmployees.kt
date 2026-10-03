package com.zera.android.model.usecase.team

import com.zera.android.model.entity.team.TeamEmployee
import com.zera.android.model.entity.user.UserRole
import com.zera.android.model.entity.user.UserStatus
import com.zera.android.model.local.SqliteManager
import com.zera.android.model.remote.client.ApiClient

class ListTeamEmployees {
    suspend fun execute(): List<TeamEmployee> {
        val managerId = SqliteManager.getUserId()
            ?: error("Não há usuário logado")
        return ApiClient.usersService.listUsers(
            role = UserRole.EMPLOYEE,
            status = UserStatus.ACTIVE,
            managerId = managerId,
            page = 0,
            size = 50,
        ).map { user ->
            TeamEmployee(
                userId = user.userId,
                name = user.name,
                role = user.role,
                status = user.status,
            )
        }
    }
}
