package com.example.android_studio_project.data.ent

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "users_trips", primaryKeys = ["tripUUID, locationUUID"])
data class TripLocation(
    @ColumnInfo(name = "tripUUID") val tripUUID: Int,
    @ColumnInfo(name = "locationUUID") val locationUUID: Int
) : Parcelable
