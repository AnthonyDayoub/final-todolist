package com.example.csci215_final.repository.impl

import com.example.csci215_final.data.local.dao.HelperDao
import com.example.csci215_final.data.local.entity.toDomain
import com.example.csci215_final.data.local.entity.toEntity
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.repository.HelperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HelperRepositoryImpl(private val helperDao: HelperDao) : HelperRepository {

    override fun getAllHelpers(): Flow<List<Helper>> =
        helperDao.getAllHelpers().map { list -> list.map { it.toDomain() } }

    override fun getHelpersForTask(taskId: Long): Flow<List<Helper>> =
        helperDao.getHelpersForTask(taskId).map { list -> list.map { it.toDomain() } }

    override suspend fun getHelperById(id: Long): Helper? =
        helperDao.getHelperById(id)?.toDomain()

    override suspend fun insertHelper(helper: Helper): Long =
        helperDao.insertHelper(helper.toEntity())

    override suspend fun updateHelper(helper: Helper) =
        helperDao.updateHelper(helper.toEntity())

    override suspend fun deleteHelper(helper: Helper) =
        helperDao.deleteHelper(helper.toEntity())
}
