package com.example.android_studio_project.data.ent

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

import java.util.*
@Parcelize
@Entity(tableName = "users_trips", primaryKeys = ["userUUID, tripUUID"])
data class UserTrip(
    @ColumnInfo(name = "userUUID") val userUUID: Int,
    @ColumnInfo(name = "tripUUID") val tripUUID: Int
) : Parcelable
