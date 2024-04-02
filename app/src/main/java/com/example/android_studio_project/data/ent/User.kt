package com.example.android_studio_project.data.ent
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

enum class UserType {
    ADMIN,
    NORMAL_USER
}
@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uuid: UUID,
    val firstName: String,
    val lastName: String,
    val avatar: String?,
    val username: String,
    val password: String,
    val email: String,
    val type: UserType
) : Parcelable
