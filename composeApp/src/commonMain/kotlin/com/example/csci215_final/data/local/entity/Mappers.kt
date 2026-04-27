package com.example.csci215_final.data.local.entity

import com.example.csci215_final.data.remote.dto.QuoteDto
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.domain.model.Quote
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.ReminderFrequency
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.domain.model.TaskWithRelations
import kotlinx.datetime.Instant

// ──────────── Task ────────────

fun TaskEntity.toDomain() = Task(
    id = id,
    title = title,
    description = description,
    dueDate = Instant.fromEpochMilliseconds(dueDateMillis),
    createdAt = Instant.fromEpochMilliseconds(createdAtMillis),
    isCompleted = isCompleted
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDateMillis = dueDate.toEpochMilliseconds(),
    createdAtMillis = createdAt.toEpochMilliseconds(),
    isCompleted = isCompleted
)

// ──────────── Helper ────────────

fun HelperEntity.toDomain() = Helper(id = id, name = name, description = description)

fun Helper.toEntity() = HelperEntity(id = id, name = name, description = description)

// ──────────── Distraction ────────────

fun DistractionEntity.toDomain() = Distraction(id = id, name = name, description = description)

fun Distraction.toEntity() = DistractionEntity(id = id, name = name, description = description)

// ──────────── Reminder ────────────

fun ReminderEntity.toDomain() = Reminder(
    id = id,
    title = title,
    message = message,
    frequency = ReminderFrequency.valueOf(frequency),
    scheduledTime = Instant.fromEpochMilliseconds(scheduledTimeMillis),
    taskId = taskId,
    isActive = isActive
)

fun Reminder.toEntity() = ReminderEntity(
    id = id,
    title = title,
    message = message,
    frequency = frequency.name,
    scheduledTimeMillis = scheduledTime.toEpochMilliseconds(),
    taskId = taskId,
    isActive = isActive
)

// ──────────── TaskWithRelations ────────────

fun TaskWithRelationsEntity.toDomain() = TaskWithRelations(
    task = task.toDomain(),
    helpers = helpers.map { it.toDomain() },
    distractions = distractions.map { it.toDomain() },
    reminders = reminders.map { it.toDomain() }
)

// ──────────── Quote ────────────

fun QuoteDto.toDomain() = Quote(text = text, author = author)
