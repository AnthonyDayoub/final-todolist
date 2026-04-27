package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.dao.ReminderDao
import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.local.entity.toEntity
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(private val reminderDao: ReminderDao) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> =
        reminderDao.getAllReminders().map { list -> list.map { it.toDomain() } }

    override fun getRemindersForTask(taskId: Long): Flow<List<Reminder>> =
        reminderDao.getRemindersForTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun getStandaloneReminders(): Flow<List<Reminder>> =
        reminderDao.getStandaloneReminders().map { list -> list.map { it.toDomain() } }

    override fun getActiveReminders(): Flow<List<Reminder>> =
        reminderDao.getActiveReminders().map { list -> list.map { it.toDomain() } }

    override suspend fun getReminderById(id: Long): Reminder? =
        reminderDao.getReminderById(id)?.toDomain()

    override suspend fun insertReminder(reminder: Reminder): Long =
        reminderDao.insertReminder(reminder.toEntity())

    override suspend fun updateReminder(reminder: Reminder) =
        reminderDao.updateReminder(reminder.toEntity())

    override suspend fun deleteReminder(reminder: Reminder) =
        reminderDao.deleteReminder(reminder.toEntity())

    override suspend fun setReminderActive(reminderId: Long, isActive: Boolean) =
        reminderDao.setReminderActive(reminderId, isActive)
}
