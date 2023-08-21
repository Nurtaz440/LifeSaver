package com.example.ambulance.repository

import com.example.ambulance.database.AppDatabase
import com.example.ambulance.model.UserLocations

class UserRepository(private val db: AppDatabase) {

    suspend fun addNote(locations: UserLocations) = db.userDao().insertLocations(locations)
    suspend fun update(locations: UserLocations) = db.userDao().updateLocation(locations)
    suspend fun delete(locations: UserLocations) = db.userDao().deleteLocation(locations)

    fun getAllLocations() = db.userDao().getAllUsers()

}