package com.example.ambulance.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPreferencesManager {
    private const val PREF_NAME = "MyAppPreferences"
    private const val KEY_REGISTERED = "registered"
    private const val PREF_EMAIL = "email"
    private const val PREF_PASS = "pass"

    lateinit var sharedPreferences: SharedPreferences

    fun setRegistered(context: Context, registered: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(KEY_REGISTERED, registered)
        editor.apply()
    }

    fun isRegistered(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
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
}