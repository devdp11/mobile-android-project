package com.example.android_studio_project.data.db

/* import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android_studio_project.data.dao.TripDao
import com.example.android_studio_project.data.dao.UserDao
import com.example.android_studio_project.data.ent.Trip
import com.example.android_studio_project.data.ent.User

@Database (entities = [Trip::class, User::class ], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: com.example.android_studio_project.data.db.Database? = null

        fun getDatabase(context: Context): com.example.android_studio_project.data.db.Database {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.android_studio_project.data.db.Database::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
} */