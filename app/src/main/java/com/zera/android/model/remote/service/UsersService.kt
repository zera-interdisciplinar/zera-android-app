package com.zera.android.model.remote.service

import com.zera.android.model.dto.user.ManagerEmployeeCountDTO
import com.zera.android.model.dto.user.SelfUserResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersService {
    @GET("users")
    suspend fun listUsers(
        @Query("role") role: String? = null,
        @Query("status") status: String? = null,
        @Query("managerId") managerId: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
    ): List<SelfUserResponseDTO>

    @GET("users/count-by-manager")
    suspend fun countByManager(): List<ManagerEmployeeCountDTO>
}
