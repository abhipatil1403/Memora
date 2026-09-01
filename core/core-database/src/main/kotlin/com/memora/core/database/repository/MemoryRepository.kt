package com.memora.core.database.repository

import com.memora.core.common.result.Result
import com.memora.core.common.storage.LocalImageStorageManager
import com.memora.core.database.dao.MemoryDao
import com.memora.core.database.mapper.toDomainModel
import com.memora.core.database.mapper.toEntity
import com.memora.core.firebase.auth.FirebaseAuthManager
import com.memora.core.firebase.firestore.FirestoreManager
import com.memora.core.model.Memory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface MemoryRepository {
    fun getAllMemories(): Flow<List<Memory>>
    fun getRecentMemories(limit: Int = 10): Flow<List<Memory>>
    fun getMemoryById(id: String): Flow<Memory?>
    suspend fun saveMemory(memory: Memory, imageBytes: ByteArray?): Result<Memory>
    suspend fun deleteMemory(memoryId: String): Result<Unit>
    suspend fun toggleFavorite(memoryId: String): Result<Unit>
}

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao,
    private val localStorageManager: LocalImageStorageManager,
    private val firestoreManager: FirestoreManager,
    private val authManager: FirebaseAuthManager
) : MemoryRepository {

    override fun getAllMemories(): Flow<List<Memory>> {
        return memoryDao.getAllMemories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getRecentMemories(limit: Int): Flow<List<Memory>> {
        return memoryDao.getRecentMemories(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMemoryById(id: String): Flow<Memory?> {
        return memoryDao.getMemoryById(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun saveMemory(memory: Memory, imageBytes: ByteArray?): Result<Memory> {
        return try {
            // 1. If imageBytes are provided, save locally in Android storage
            var finalImageUrl = memory.imageUrl
            var finalThumbnailUrl = memory.thumbnailUrl

            if (imageBytes != null && imageBytes.isNotEmpty()) {
                when (val localResult = localStorageManager.saveMemoryImage(memory.id, imageBytes)) {
                    is Result.Success -> {
                        finalImageUrl = localResult.data
                        finalThumbnailUrl = localResult.data
                    }
                    is Result.Error -> {
                        Timber.w(localResult.exception, "Failed to save image locally, proceeding with default path")
                    }
                    Result.Loading -> {}
                }
            }

            val updatedMemory = memory.copy(
                imageUrl = finalImageUrl,
                thumbnailUrl = finalThumbnailUrl
            )

            // 2. Save locally in Room Database
            memoryDao.insertMemory(updatedMemory.toEntity())

            // 3. Sync metadata to Firestore Database if authenticated
            val user = authManager.getCurrentUser()
            if (user != null) {
                firestoreManager.saveMemory(user.id, updatedMemory)
            }

            Result.Success(updatedMemory)
        } catch (e: Exception) {
            Timber.e(e, "Error in MemoryRepository.saveMemory")
            Result.Error(e)
        }
    }

    override suspend fun deleteMemory(memoryId: String): Result<Unit> {
        return try {
            // 1. Delete local file
            localStorageManager.deleteMemoryImage(memoryId)

            // 2. Mark deleted in Room
            memoryDao.softDelete(memoryId)

            // 3. Sync delete to Firestore
            val user = authManager.getCurrentUser()
            if (user != null) {
                firestoreManager.deleteMemory(user.id, memoryId)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting memory in repository")
            Result.Error(e)
        }
    }

    override suspend fun toggleFavorite(memoryId: String): Result<Unit> {
        return try {
            memoryDao.toggleFavorite(memoryId)
            val user = authManager.getCurrentUser()
            if (user != null) {
                val entity = memoryDao.getMemoryByIdSync(memoryId)
                if (entity != null) {
                    firestoreManager.saveMemory(user.id, entity.toDomainModel())
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite status")
            Result.Error(e)
        }
    }
}
