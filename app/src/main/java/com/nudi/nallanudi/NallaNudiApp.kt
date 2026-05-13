package com.nudi.nallanudi

import android.app.Application
import com.google.firebase.FirebaseApp

class NallaNudiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
