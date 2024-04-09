package com.example.android_studio_project.data.ent
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true) val uuid: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date")val date: Date
) : Parcelable
