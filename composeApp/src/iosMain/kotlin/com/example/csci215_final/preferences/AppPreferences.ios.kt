package com.example.csci215_final.preferences

import platform.Foundation.NSUserDefaults

actual class AppPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun isFirstLaunch(): Boolean = !defaults.boolForKey("app_launched")

    actual fun markLaunched() {
        defaults.setBool(true, forKey = "app_launched")
    }
}
