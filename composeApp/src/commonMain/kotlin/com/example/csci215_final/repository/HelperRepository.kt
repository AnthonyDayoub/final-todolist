package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Helper
import kotlinx.coroutines.flow.Flow

interface HelperRepository {
    fun getAllHelpers(): Flow<List<Helper>>
    fun getHelpersForTask(taskId: Long): Flow<List<Helper>>
    suspend fun getHelperById(id: Long): Helper?
    suspend fun insertHelper(helper: Helper): Long
    suspend fun updateHelper(helper: Helper)
    suspend fun deleteHelper(helper: Helper)
}
