package com.example.android_studio_project.data.retrofit.models

data class UserModel(
    val firstName: String,
    val lastName: String,
    val avatar: String?,
    val username: String,
    val password: String,
    val email: String,
    val type: Boolean = false
)
