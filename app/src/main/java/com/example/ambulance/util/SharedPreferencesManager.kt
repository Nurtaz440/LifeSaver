package com.example.ambulance.util

import android.content.Context
import android.content.SharedPreferences

object  SharedPreferencesManager {
    private const val PREF_NAME = "MyAppPreferences"
    private const val KEY_REGISTERED = "registered"


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
}