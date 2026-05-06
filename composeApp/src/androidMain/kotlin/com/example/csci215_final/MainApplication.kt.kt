package com.example.csci215_final

import android.app.Application
import com.example.csci215_final.data.local.database.DatabaseFactory
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.preferences.AppPreferences

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(DatabaseFactory(this).create(), AppPreferences(this))
    }
}
