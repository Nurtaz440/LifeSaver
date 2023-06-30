package com.example.ambulance.repository

import androidx.lifecycle.LiveData
import com.example.ambulance.database.UserDao
import com.example.ambulance.model.UserDetails

class UserRepository(private val userDao: UserDao) {
    val allUsers: LiveData<UserDetails> = userDao.getAllUsers()
    suspend fun insertUser(user: UserDetails) {
        userDao.insertUser(user)
    }
}