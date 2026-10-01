package com.zera.android.model.usecase.user

import com.zera.android.model.dto.user.SelfUserResponseDTO
import com.zera.android.model.entity.user.ProfileUser
import com.zera.android.model.usecase.auth.GetSelfUser

class GetProfile(
    private val getSelfUser: GetSelfUser = GetSelfUser(),
) {
    suspend fun execute(): ProfileUser = fromDto(getSelfUser.execute())

    companion object {
        fun fromDto(dto: SelfUserResponseDTO): ProfileUser = ProfileUser(
            userId = dto.userId,
            name = dto.name,
            email = dto.email,
            role = dto.role,
            imageUrl = dto.imageUrl,
        )
    }
}
