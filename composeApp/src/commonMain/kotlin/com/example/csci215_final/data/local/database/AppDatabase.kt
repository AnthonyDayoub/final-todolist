package com.example.csci215_final.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.csci215_final.data.local.dao.ReminderDao
import com.example.csci215_final.data.local.dao.TaskDao
import com.example.csci215_final.data.local.entity.DistractionEntity
import com.example.csci215_final.data.local.entity.HelperEntity
import com.example.csci215_final.data.local.entity.ReminderEntity
import com.example.csci215_final.data.local.entity.TaskDistractionCrossRef
import com.example.csci215_final.data.local.entity.TaskEntity
import com.example.csci215_final.data.local.entity.TaskHelperCrossRef

@Database(
    entities = [
        TaskEntity::class,
        HelperEntity::class,
        DistractionEntity::class,
        ReminderEntity::class,
        TaskHelperCrossRef::class,
        TaskDistractionCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun reminderDao(): ReminderDao
}
