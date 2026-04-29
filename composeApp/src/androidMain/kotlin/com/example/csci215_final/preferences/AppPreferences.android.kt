package com.example.csci215_final.preferences

import android.content.Context

actual class AppPreferences(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    actual fun isFirstLaunch(): Boolean = prefs.getBoolean("first_launch", true)

    actual fun markLaunched() {
        prefs.edit().putBoolean("first_launch", false).apply()
    }
}
