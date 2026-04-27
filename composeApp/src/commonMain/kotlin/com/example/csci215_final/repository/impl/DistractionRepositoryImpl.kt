package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.dao.DistractionDao
import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.local.entity.toEntity
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.repository.DistractionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DistractionRepositoryImpl(private val distractionDao: DistractionDao) : DistractionRepository {

    override fun getAllDistractions(): Flow<List<Distraction>> =
        distractionDao.getAllDistractions().map { list -> list.map { it.toDomain() } }

    override fun getDistractionsForTask(taskId: Long): Flow<List<Distraction>> =
        distractionDao.getDistractionsForTask(taskId).map { list -> list.map { it.toDomain() } }

    override suspend fun getDistractionById(id: Long): Distraction? =
        distractionDao.getDistractionById(id)?.toDomain()

    override suspend fun insertDistraction(distraction: Distraction): Long =
        distractionDao.insertDistraction(distraction.toEntity())

    override suspend fun updateDistraction(distraction: Distraction) =
        distractionDao.updateDistraction(distraction.toEntity())

    override suspend fun deleteDistraction(distraction: Distraction) =
        distractionDao.deleteDistraction(distraction.toEntity())
}
