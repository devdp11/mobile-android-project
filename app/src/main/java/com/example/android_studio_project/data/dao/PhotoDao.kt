package com.example.android_studio_project.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.android_studio_project.data.ent.Photo

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)
}