package com.example.csci215_final.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.csci215_final.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY scheduled_time_millis ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE task_id = :taskId ORDER BY scheduled_time_millis ASC")
    fun getRemindersForTask(taskId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE task_id IS NULL ORDER BY scheduled_time_millis ASC")
    fun getStandaloneReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE is_active = 1 ORDER BY scheduled_time_millis ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>

    @Query("UPDATE reminders SET is_active = :isActive WHERE id = :reminderId")
    suspend fun setReminderActive(reminderId: Long, isActive: Boolean)
}
