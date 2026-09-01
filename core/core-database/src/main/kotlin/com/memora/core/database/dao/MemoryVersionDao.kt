package com.memora.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memora.core.database.entity.MemoryVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryVersionDao {
    @Query("SELECT * FROM memory_versions WHERE memoryId = :memoryId ORDER BY editedAt DESC")
    fun getVersionsForMemory(memoryId: String): Flow<List<MemoryVersionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: MemoryVersionEntity)

    @Query("SELECT * FROM memory_versions WHERE id = :versionId")
    suspend fun getVersionById(versionId: String): MemoryVersionEntity?
}
