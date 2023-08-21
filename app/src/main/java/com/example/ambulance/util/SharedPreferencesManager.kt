package com.example.ambulance.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPreferencesManager {
    private const val PREF_NAME = "MyAppPreferences"
    private const val PREF_NAME_Boolean = "MyAppPreferences4"
    private const val PREF_LOCATION = "MyLocation"
    private const val KEY_REGISTERED = "registered"
    private const val PREF_EMAIL = "email"
    private const val PREF_PASS = "pass"
    private const val PREF_LONGETUTDE = "long"
    private const val PREF_LATITUDE = "lat"

    lateinit var sharedPreferences: SharedPreferences

    fun setRegistered(context: Context, registered: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME_Boolean, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(KEY_REGISTERED, registered)
        editor.apply()
    }

    fun isRegistered(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME_Boolean, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_REGISTERED, false)
    }


    fun setEmail(context: Context, email: String, pass: String) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(PREF_EMAIL, email)
            putString(PREF_PASS, pass)
        }
    }

    fun getEmail(context: Context): Pair<String?,String?> {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
       val email =  sharedPreferences.getString(PREF_EMAIL, null)
       val pass = sharedPreferences.getString(PREF_PASS, null)
        return Pair(email,pass)
    }

    fun setCurrentLocation(context: Context,lat:String,long: String) {
        sharedPreferences = context.getSharedPreferences(PREF_LOCATION, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(PREF_LATITUDE, lat)
            putString(PREF_LONGETUTDE, long)
        }
    }

    fun getLocation(context: Context): Pair<String?,String?> {
        sharedPreferences = context.getSharedPreferences(PREF_LOCATION, Context.MODE_PRIVATE)
        val lat = sharedPreferences.getString(PREF_LATITUDE, null)
        val long =  sharedPreferences.getString(PREF_LONGETUTDE, null)
        return Pair(lat,long)
    }

}