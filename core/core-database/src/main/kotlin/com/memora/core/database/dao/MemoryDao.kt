package com.memora.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.memora.core.database.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentMemories(limit: Int): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE id = :id")
    fun getMemoryById(id: String): Flow<MemoryEntity?>

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryByIdSync(id: String): MemoryEntity?

    @Query("SELECT * FROM memories WHERE category = :category AND isDeleted = 0 AND isArchived = 0 ORDER BY createdAt DESC")
    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>>

    @Query("""
        SELECT * FROM memories 
        WHERE isDeleted = 0 
        AND (title LIKE '%' || :query || '%' 
             OR summary LIKE '%' || :query || '%' 
             OR tags LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedMemories(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE isFavorite = 1 AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getFavoriteMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isArchived = 1 AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getArchivedMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE importance >= :minImportance AND isDeleted = 0 AND isArchived = 0 ORDER BY importance DESC")
    fun getMemoriesByImportance(minImportance: Double): Flow<List<MemoryEntity>>

    // Smart collection queries
    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND createdAt >= :startOfDay ORDER BY createdAt DESC")
    fun getMemoriesToday(startOfDay: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND createdAt >= :startOfWeek ORDER BY createdAt DESC")
    fun getMemoriesThisWeek(startOfWeek: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND category IN ('WHITEBOARD', 'NOTICE', 'TIMETABLE', 'CERTIFICATE') ORDER BY createdAt DESC")
    fun getCollegeMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND category IN ('RECEIPT') ORDER BY createdAt DESC")
    fun getReceiptMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND category IN ('CARD') ORDER BY createdAt DESC")
    fun getBusinessMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND category IN ('PRESCRIPTION') ORDER BY createdAt DESC")
    fun getMedicalMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>)

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("UPDATE memories SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("UPDATE memories SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE memories SET isFavorite = NOT isFavorite, updatedAt = :now WHERE id = :id")
    suspend fun toggleFavorite(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE memories SET isArchived = 1, updatedAt = :now WHERE id = :id")
    suspend fun archive(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE memories SET viewCount = viewCount + 1, lastViewedAt = :now WHERE id = :id")
    suspend fun recordView(id: String, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM memories WHERE isDeleted = 0")
    fun getMemoryCount(): Flow<Int>

    // Counts for smart collections
    @Query("SELECT COUNT(*) FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND createdAt >= :startOfDay")
    suspend fun countMemoriesToday(startOfDay: Long): Int

    @Query("SELECT COUNT(*) FROM memories WHERE isDeleted = 0 AND isArchived = 0 AND createdAt >= :startOfWeek")
    suspend fun countMemoriesThisWeek(startOfWeek: Long): Int

    @Query("SELECT COUNT(*) FROM memories WHERE isFavorite = 1 AND isDeleted = 0")
    suspend fun countFavorites(): Int

    @Query("SELECT COUNT(*) FROM memories WHERE isArchived = 1 AND isDeleted = 0")
    suspend fun countArchived(): Int

    @Query("SELECT COUNT(*) FROM memories WHERE importance >= 0.8 AND isDeleted = 0 AND isArchived = 0")
    suspend fun countImportant(): Int
}
