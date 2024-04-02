package com.example.android_studio_project.data.ent

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

import java.util.*
@Parcelize
@Entity(tableName = "users_trips", primaryKeys = ["userUuid, tripUuid"])
data class UserTrip(
    val userUuid: UUID,
    val tripUuid: UUID
) : Parcelable
