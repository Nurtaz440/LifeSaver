package com.example.ambulance.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "notes")
@Parcelize
data class UserLocations(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var city: String,
    var street: String,
    var village: String,
    var home: String
) : Parcelable