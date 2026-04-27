package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.domain.model.TaskWithRelations
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getAllTasksWithRelations(): Flow<List<TaskWithRelations>>
    fun getTasksForDateRange(startMillis: Long, endMillis: Long): Flow<List<Task>>
    fun getIncompleteTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Long): Task?
    suspend fun getTaskWithRelations(id: Long): TaskWithRelations?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun setHelperForTask(taskId: Long, helperId: Long)
    suspend fun removeHelperFromTask(taskId: Long, helperId: Long)
    suspend fun replaceHelpersForTask(taskId: Long, helperIds: List<Long>)
    suspend fun setDistractionForTask(taskId: Long, distractionId: Long)
    suspend fun removeDistractionFromTask(taskId: Long, distractionId: Long)
    suspend fun replaceDistractionsForTask(taskId: Long, distractionIds: List<Long>)
}
