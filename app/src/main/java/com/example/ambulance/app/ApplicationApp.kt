package com.example.ambulance.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.yandex.mapkit.MapKitFactory

class ApplicationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        MapKitFactory.setApiKey("f05de5b6-07f3-4916-aee7-afe6c8a1c7a0")
        MapKitFactory.initialize(this)
    }
}