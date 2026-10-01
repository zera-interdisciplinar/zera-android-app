package com.zera.android.model.entity.user

data class ProfileUser(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val imageUrl: String?,
    val telephoneId: String? = null,
    val phone: String = "",
)
