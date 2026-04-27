package com.example.csci215_final.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.csci215_final.data.local.entity.HelperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HelperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHelper(helper: HelperEntity): Long

    @Update
    suspend fun updateHelper(helper: HelperEntity)

    @Delete
    suspend fun deleteHelper(helper: HelperEntity)

    @Query("SELECT * FROM helpers ORDER BY name ASC")
    fun getAllHelpers(): Flow<List<HelperEntity>>

    @Query("SELECT * FROM helpers WHERE id = :helperId")
    suspend fun getHelperById(helperId: Long): HelperEntity?

    @Query("""
        SELECT h.* FROM helpers h
        INNER JOIN task_helper_cross_ref ref ON h.id = ref.helper_id
        WHERE ref.task_id = :taskId
    """)
    fun getHelpersForTask(taskId: Long): Flow<List<HelperEntity>>
}
