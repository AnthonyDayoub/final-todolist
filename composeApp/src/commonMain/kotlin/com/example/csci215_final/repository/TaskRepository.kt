package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.domain.model.TaskWithRelations
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksWithRelations(): Flow<List<TaskWithRelations>>

    fun getCompletedTasks(): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?

    suspend fun getTaskWithRelations(id: Long): TaskWithRelations?

    suspend fun insertTask(task: Task): Long

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)
}
