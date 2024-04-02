package com.example.android_studio_project.data.ent
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey val uuid: UUID,
    val description: String,
    val date: Date
) : Parcelable
