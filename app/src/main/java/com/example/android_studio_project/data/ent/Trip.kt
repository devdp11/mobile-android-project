package com.example.android_studio_project.data.ent

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey val uuid: UUID,
    val description: String,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val rating: Int,
    val locations: MutableList<Location> = mutableListOf()
) : Parcelable
