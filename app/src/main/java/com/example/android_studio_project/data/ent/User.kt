package com.example.android_studio_project.data.ent
import java.util.*
data class User(
    val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val avatar: String?,
    val username: String,
    val password: String,
    val email: String
)
