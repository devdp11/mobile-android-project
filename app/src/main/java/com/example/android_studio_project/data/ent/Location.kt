package com.example.android_studio_project.data.ent
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val uuid: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "photos") val photos: MutableList<Photo> = mutableListOf()
) : Parcelable
