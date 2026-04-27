package com.example.csci215_final.repository

import com.example.csci215_final.domain.model.Distraction
import kotlinx.coroutines.flow.Flow

interface DistractionRepository {
    fun getAllDistractions(): Flow<List<Distraction>>
    fun getDistractionsForTask(taskId: Long): Flow<List<Distraction>>
    suspend fun getDistractionById(id: Long): Distraction?
    suspend fun insertDistraction(distraction: Distraction): Long
    suspend fun updateDistraction(distraction: Distraction)
    suspend fun deleteDistraction(distraction: Distraction)
}
