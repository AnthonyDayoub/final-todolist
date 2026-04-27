package com.example.csci215_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "task_distraction_cross_ref",
    primaryKeys = ["task_id", "distraction_id"],
    indices = [Index("distraction_id")]
)
data class TaskDistractionCrossRef(
    @ColumnInfo(name = "task_id") val taskId: Long,
    @ColumnInfo(name = "distraction_id") val distractionId: Long
)
