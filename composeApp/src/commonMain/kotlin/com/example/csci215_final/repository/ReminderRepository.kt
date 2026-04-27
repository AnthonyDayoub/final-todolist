package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun getRemindersForTask(taskId: Long): Flow<List<Reminder>>
    fun getStandaloneReminders(): Flow<List<Reminder>>
    fun getActiveReminders(): Flow<List<Reminder>>
    suspend fun getReminderById(id: Long): Reminder?
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun setReminderActive(reminderId: Long, isActive: Boolean)
}
