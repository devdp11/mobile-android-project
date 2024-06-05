package com.example.android_studio_project.data.room.ent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uuid: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val avatar: String?,
    val email: String
)