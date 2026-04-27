package com.example.csci215_final.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = context.getDatabasePath("todolist.db").absolutePath
        ).build()
    }
}
