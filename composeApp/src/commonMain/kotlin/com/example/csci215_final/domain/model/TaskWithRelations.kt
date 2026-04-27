package com.example.csci215_final.domain.model

data class TaskWithRelations(
    val task: Task,
    val helpers: List<Helper>,
    val distractions: List<Distraction>,
    val reminders: List<Reminder>
)
