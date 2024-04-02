package com.example.android_studio_project.data.ent

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "users_trips", primaryKeys = ["locationUuid, tripUuid"])
data class TripLocation(
    val tripUuid: UUID,
    val locationUuid: UUID
) : Parcelable
