package com.example.csci215_final.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.csci215_final.data.local.entity.TaskEntity
import com.example.csci215_final.data.local.entity.TaskWithRelationsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE is_completed = 1 ORDER BY due_date_millis DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithRelations(taskId: Long): TaskWithRelationsEntity?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY due_date_millis ASC")
    fun getAllTasksWithRelations(): Flow<List<TaskWithRelationsEntity>>
}
