package com.example.android_studio_project.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.android_studio_project.data.ent.Location
import com.example.android_studio_project.data.ent.TripLocation

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocation(location: Location)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateLocation(location: Location)

    @Delete
    suspend fun deleteLocation(location: Location)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTripLocation(tripLocation: TripLocation)

    @Delete
    suspend fun deleteTripLocation(tripLocation: TripLocation)
}