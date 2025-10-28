package com.project.avara

import android.app.Application
import com.google.firebase.FirebaseApp

class AvaraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
