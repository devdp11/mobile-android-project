package com.example.android_studio_project.data.room.ent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uuid: UUID,
    val firstName: String?,
    val lastName: String?,
    val username: String?,
    val avatar: String?,
    val email: String?,
    val password: String?
)