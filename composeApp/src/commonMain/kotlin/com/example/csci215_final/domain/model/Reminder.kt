package com.example.csci215_final.domain.model

import kotlinx.datetime.Instant

data class Reminder(
    val id: Long = 0,
    val title: String,
    val message: String = "",
    val frequency: ReminderFrequency,
    val scheduledTime: Instant,
    val taskId: Long? = null,
    val isActive: Boolean = true,
)
