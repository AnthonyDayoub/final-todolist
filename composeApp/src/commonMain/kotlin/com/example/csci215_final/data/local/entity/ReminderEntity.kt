package com.example.csci215_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("task_id")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "message") val message: String = "",
    @ColumnInfo(name = "frequency") val frequency: String,   // ReminderFrequency name()
    @ColumnInfo(name = "scheduled_time_millis") val scheduledTimeMillis: Long,
    @ColumnInfo(name = "task_id") val taskId: Long? = null,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
