package com.example.android_studio_project.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.android_studio_project.data.ent.Trip

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrip(trip: Trip)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)
}