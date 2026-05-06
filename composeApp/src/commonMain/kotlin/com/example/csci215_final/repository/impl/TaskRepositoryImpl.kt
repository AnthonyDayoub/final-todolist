package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.dao.TaskDao
import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.local.entity.toEntity
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
) : TaskRepository {
    override fun getAllTasksWithRelations(): Flow<List<TaskWithRelations>> =
        taskDao.getAllTasksWithRelations().map { list -> list.map { it.toDomain() } }

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks().map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getTaskById(id)?.toDomain()

    override suspend fun getTaskWithRelations(id: Long): TaskWithRelations? =
        taskDao.getTaskWithRelations(id)?.toDomain()

    override suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(task.toEntity())

    override suspend fun updateTask(task: Task) =
        taskDao.updateTask(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(task.toEntity())
}
