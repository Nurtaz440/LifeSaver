package com.example.ambulance.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserDetails (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = null,
    val surName: String? = null,
    val number: String? = null
    )