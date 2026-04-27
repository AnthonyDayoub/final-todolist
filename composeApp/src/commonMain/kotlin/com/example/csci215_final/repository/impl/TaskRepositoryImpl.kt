package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.dao.TaskDao
import com.example.csci215_final.data.local.entity.TaskDistractionCrossRef
import com.example.csci215_final.data.local.entity.TaskHelperCrossRef
import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.local.entity.toEntity
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { it.toDomain() } }

    override fun getAllTasksWithRelations(): Flow<List<TaskWithRelations>> =
        taskDao.getAllTasksWithRelations().map { list -> list.map { it.toDomain() } }

    override fun getTasksForDateRange(startMillis: Long, endMillis: Long): Flow<List<Task>> =
        taskDao.getTasksForDateRange(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override fun getIncompleteTasks(): Flow<List<Task>> =
        taskDao.getIncompleteTasks().map { list -> list.map { it.toDomain() } }

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

    override suspend fun setHelperForTask(taskId: Long, helperId: Long) =
        taskDao.insertTaskHelperCrossRef(TaskHelperCrossRef(taskId, helperId))

    override suspend fun removeHelperFromTask(taskId: Long, helperId: Long) =
        taskDao.deleteTaskHelperCrossRef(TaskHelperCrossRef(taskId, helperId))

    override suspend fun replaceHelpersForTask(taskId: Long, helperIds: List<Long>) {
        taskDao.deleteAllHelpersForTask(taskId)
        helperIds.forEach { taskDao.insertTaskHelperCrossRef(TaskHelperCrossRef(taskId, it)) }
    }

    override suspend fun setDistractionForTask(taskId: Long, distractionId: Long) =
        taskDao.insertTaskDistractionCrossRef(TaskDistractionCrossRef(taskId, distractionId))

    override suspend fun removeDistractionFromTask(taskId: Long, distractionId: Long) =
        taskDao.deleteTaskDistractionCrossRef(TaskDistractionCrossRef(taskId, distractionId))

    override suspend fun replaceDistractionsForTask(taskId: Long, distractionIds: List<Long>) {
        taskDao.deleteAllDistractionsForTask(taskId)
        distractionIds.forEach { taskDao.insertTaskDistractionCrossRef(TaskDistractionCrossRef(taskId, it)) }
    }
}
