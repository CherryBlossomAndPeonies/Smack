package com.example.smack.controller

import android.app.Application
import com.example.smack.utilities.SharedPrefs

class App : Application() {
    companion object {
        lateinit var sharedPrefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(applicationContext) // available for all the contexts through out the app
    }
}