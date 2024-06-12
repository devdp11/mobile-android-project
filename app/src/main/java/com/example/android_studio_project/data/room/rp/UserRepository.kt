package com.example.android_studio_project.data.room.rp

import androidx.lifecycle.LiveData
import com.example.android_studio_project.data.room.dao.UserDao
import com.example.android_studio_project.data.room.ent.User

class UserRepository(private val userDao: UserDao) {
    val readAllUsers: LiveData<List<User>> = userDao.readAllUsers()

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}
