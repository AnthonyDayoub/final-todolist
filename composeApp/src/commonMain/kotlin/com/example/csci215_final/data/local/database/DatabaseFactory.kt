package com.example.csci215_final.data.local.database

expect class DatabaseFactory {
    fun create(): AppDatabase
}
