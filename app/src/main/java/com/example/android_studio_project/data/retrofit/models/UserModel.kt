package com.example.android_studio_project.data.retrofit.models

import java.util.UUID

data class UserModel(
    val uuid: UUID?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?,
    val username: String?,
    val password: String?,
    val email: String?,
    val type: Boolean = false
)

