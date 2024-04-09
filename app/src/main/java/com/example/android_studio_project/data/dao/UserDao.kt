package com.example.android_studio_project.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android_studio_project.data.ent.TripLocation
import com.example.android_studio_project.data.ent.User
import com.example.android_studio_project.data.ent.UserTrip

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserTrip(userTrip: UserTrip)

    @Delete
    suspend fun deleteUserTrip(userTrip: UserTrip)
}