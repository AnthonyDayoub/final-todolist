package com.example.csci215_final.preferences

expect class AppPreferences {
    fun isFirstLaunch(): Boolean

    fun markLaunched()
}
