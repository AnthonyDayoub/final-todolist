package com.example.csci215_final.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "task_helper_cross_ref",
    primaryKeys = ["task_id", "helper_id"],
    indices = [Index("helper_id")]
)
data class TaskHelperCrossRef(
    @ColumnInfo(name = "task_id") val taskId: Long,
    @ColumnInfo(name = "helper_id") val helperId: Long
)
