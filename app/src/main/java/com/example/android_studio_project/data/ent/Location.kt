package com.example.android_studio_project.data.ent
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey val uuid: UUID,
    val name: String,
    val description: String,
    val type: Int,
    val rating: Int,
    val latitude: Double,
    val longitude: Double,
    val photos: MutableList<Photo> = mutableListOf()
) : Parcelable
