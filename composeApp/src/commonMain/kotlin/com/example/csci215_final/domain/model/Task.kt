package com.example.csci215_final.domain.model

import kotlinx.datetime.Instant

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Instant,
    val createdAt: Instant,
    val isCompleted: Boolean = false,
)
