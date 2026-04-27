package com.example.csci215_final.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithRelationsEntity(
    @Embedded val task: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskHelperCrossRef::class,
            parentColumn = "task_id",
            entityColumn = "helper_id"
        )
    )
    val helpers: List<HelperEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskDistractionCrossRef::class,
            parentColumn = "task_id",
            entityColumn = "distraction_id"
        )
    )
    val distractions: List<DistractionEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "task_id"
    )
    val reminders: List<ReminderEntity>
)
