package com.example.csci215_final.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.csci215_final.data.local.entity.DistractionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DistractionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistraction(distraction: DistractionEntity): Long

    @Update
    suspend fun updateDistraction(distraction: DistractionEntity)

    @Delete
    suspend fun deleteDistraction(distraction: DistractionEntity)

    @Query("SELECT * FROM distractions ORDER BY name ASC")
    fun getAllDistractions(): Flow<List<DistractionEntity>>

    @Query("SELECT * FROM distractions WHERE id = :distractionId")
    suspend fun getDistractionById(distractionId: Long): DistractionEntity?

    @Query("""
        SELECT d.* FROM distractions d
        INNER JOIN task_distraction_cross_ref ref ON d.id = ref.distraction_id
        WHERE ref.task_id = :taskId
    """)
    fun getDistractionsForTask(taskId: Long): Flow<List<DistractionEntity>>
}
