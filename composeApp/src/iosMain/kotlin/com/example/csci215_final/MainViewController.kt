package com.example.csci215_final

import androidx.compose.ui.window.ComposeUIViewController
import com.example.csci215_final.data.local.database.DatabaseFactory
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.preferences.AppPreferences

fun MainViewController() = ComposeUIViewController {
    ServiceLocator.init(DatabaseFactory().create(), AppPreferences())
    App()
}