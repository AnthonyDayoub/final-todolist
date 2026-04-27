package com.example.csci215_final.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.csci215_final.data.local.entity.TaskDistractionCrossRef
import com.example.csci215_final.data.local.entity.TaskEntity
import com.example.csci215_final.data.local.entity.TaskHelperCrossRef
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

    @Query("SELECT * FROM tasks ORDER BY due_date_millis ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE due_date_millis BETWEEN :startMillis AND :endMillis ORDER BY due_date_millis ASC")
    fun getTasksForDateRange(startMillis: Long, endMillis: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE is_completed = 0 ORDER BY due_date_millis ASC")
    fun getIncompleteTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskWithRelations(taskId: Long): TaskWithRelationsEntity?

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY due_date_millis ASC")
    fun getAllTasksWithRelations(): Flow<List<TaskWithRelationsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskHelperCrossRef(crossRef: TaskHelperCrossRef)

    @Delete
    suspend fun deleteTaskHelperCrossRef(crossRef: TaskHelperCrossRef)

    @Query("DELETE FROM task_helper_cross_ref WHERE task_id = :taskId")
    suspend fun deleteAllHelpersForTask(taskId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskDistractionCrossRef(crossRef: TaskDistractionCrossRef)

    @Delete
    suspend fun deleteTaskDistractionCrossRef(crossRef: TaskDistractionCrossRef)

    @Query("DELETE FROM task_distraction_cross_ref WHERE task_id = :taskId")
    suspend fun deleteAllDistractionsForTask(taskId: Long)
}
