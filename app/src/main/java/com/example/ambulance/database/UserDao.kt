package com.example.ambulance.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ambulance.model.UserLocations

@Dao
interface UserDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<UserLocations>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(user: UserLocations)

    @Update
    suspend fun updateLocation(location:UserLocations)

    @Delete
    suspend fun deleteLocation(location: UserLocations)


}