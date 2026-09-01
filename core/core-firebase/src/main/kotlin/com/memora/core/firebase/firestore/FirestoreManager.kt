package com.memora.core.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.memora.core.common.result.Result
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.model.MemoryVersion
import com.memora.core.model.Reminder
import com.memora.core.model.SmartCollection
import com.memora.core.model.Tag
import com.memora.core.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore?
) {
    suspend fun saveUserProfile(user: User): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            val userMap = hashMapOf(
                "id" to user.id,
                "email" to user.email,
                "displayName" to user.displayName,
                "photoUrl" to user.photoUrl,
                "createdAt" to user.createdAt.toEpochMilli(),
                "updatedAt" to user.updatedAt.toEpochMilli()
            )
            db.collection("users").document(user.id)
                .set(userMap, SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving user profile")
            Result.Error(e)
        }
    }

    suspend fun saveMemory(userId: String, memory: Memory): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            val memoryMap = hashMapOf(
                "id" to memory.id,
                "imageUrl" to memory.imageUrl,
                "thumbnailUrl" to memory.thumbnailUrl,
                "title" to memory.title,
                "summary" to memory.summary,
                "category" to memory.category.name,
                "importance" to memory.importance,
                "overallConfidence" to memory.overallConfidence,
                "detectedLanguage" to memory.detectedLanguage,
                "tags" to memory.tags.map { it.value },
                "isSynced" to true,
                "isFavorite" to memory.isFavorite,
                "isArchived" to memory.isArchived,
                "viewCount" to memory.viewCount,
                "capturedAt" to memory.capturedAt.toEpochMilli(),
                "createdAt" to memory.createdAt.toEpochMilli(),
                "updatedAt" to memory.updatedAt.toEpochMilli()
            )
            db.collection("users").document(userId)
                .collection("memories").document(memory.id)
                .set(memoryMap, SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving memory to Firestore")
            Result.Error(e)
        }
    }

    fun getMemoriesFlow(userId: String): Flow<Result<List<Memory>>> {
        val db = firestore ?: return flowOf(Result.Success(emptyList()))
        return callbackFlow {
            val listener = db.collection("users").document(userId)
                .collection("memories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val memories = snapshot.documents.mapNotNull { doc ->
                            mapDocumentToMemory(doc.data ?: return@mapNotNull null)
                        }
                        trySend(Result.Success(memories))
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    suspend fun getMemoryById(userId: String, memoryId: String): Result<Memory?> {
        val db = firestore ?: return Result.Success(null)
        return try {
            val doc = db.collection("users").document(userId)
                .collection("memories").document(memoryId)
                .get().await()
            val data = doc.data
            if (data != null) {
                Result.Success(mapDocumentToMemory(data))
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching memory by ID")
            Result.Error(e)
        }
    }

    suspend fun deleteMemory(userId: String, memoryId: String): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            db.collection("users").document(userId)
                .collection("memories").document(memoryId)
                .delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting memory")
            Result.Error(e)
        }
    }

    suspend fun saveMemoryVersion(userId: String, memoryId: String, version: MemoryVersion): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            val versionMap = hashMapOf(
                "id" to version.id,
                "editedAt" to version.editedAt.toEpochMilli(),
                "editedBy" to version.editedBy,
                "changeDescription" to version.changeDescription,
                "snapshot" to hashMapOf(
                    "title" to version.fields.title,
                    "summary" to version.fields.summary,
                    "category" to version.fields.category,
                    "tags" to version.fields.tags
                )
            )
            db.collection("users").document(userId)
                .collection("memories").document(memoryId)
                .collection("versions").document(version.id)
                .set(versionMap)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving memory version")
            Result.Error(e)
        }
    }

    suspend fun saveCollection(userId: String, collection: SmartCollection): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            val collectionMap = hashMapOf(
                "type" to collection.type.name,
                "name" to collection.name,
                "count" to collection.count,
                "icon" to collection.icon
            )
            db.collection("users").document(userId)
                .collection("collections").document(collection.type.name)
                .set(collectionMap, SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving collection")
            Result.Error(e)
        }
    }

    suspend fun saveReminder(userId: String, reminder: Reminder): Result<Unit> {
        val db = firestore ?: return Result.Success(Unit)
        return try {
            val reminderMap = hashMapOf(
                "id" to reminder.id,
                "memoryId" to reminder.memoryId,
                "title" to reminder.title,
                "description" to reminder.description,
                "dateTime" to reminder.dateTime.toEpochMilli(),
                "isCompleted" to reminder.isCompleted,
                "source" to reminder.source,
                "createdAt" to reminder.createdAt.toEpochMilli(),
                "updatedAt" to reminder.updatedAt.toEpochMilli()
            )
            db.collection("users").document(userId)
                .collection("reminders").document(reminder.id)
                .set(reminderMap, SetOptions.merge())
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving reminder")
            Result.Error(e)
        }
    }

    fun getRemindersFlow(userId: String): Flow<Result<List<Reminder>>> {
        val db = firestore ?: return flowOf(Result.Success(emptyList()))
        return callbackFlow {
            val listener = db.collection("users").document(userId)
                .collection("reminders")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val reminders = snapshot.documents.mapNotNull { doc ->
                            mapDocumentToReminder(doc.data ?: return@mapNotNull null)
                        }
                        trySend(Result.Success(reminders))
                    }
                }
            awaitClose { listener.remove() }
        }
    }

    private fun mapDocumentToMemory(data: Map<String, Any>): Memory? {
        return try {
            val id = data["id"] as? String ?: return null
            val categoryStr = data["category"] as? String ?: Category.OTHER.name
            val category = try { Category.valueOf(categoryStr) } catch (_: Exception) { Category.OTHER }
            val tagsList = (data["tags"] as? List<*>)?.mapNotNull { it as? String }?.map { Tag(it) } ?: emptyList()

            Memory(
                id = id,
                imageUrl = data["imageUrl"] as? String ?: "",
                thumbnailUrl = data["thumbnailUrl"] as? String ?: "",
                title = data["title"] as? String ?: "",
                summary = data["summary"] as? String ?: "",
                category = category,
                importance = (data["importance"] as? Number)?.toDouble() ?: 0.5,
                overallConfidence = (data["overallConfidence"] as? Number)?.toDouble() ?: 0.9,
                detectedLanguage = data["detectedLanguage"] as? String ?: "en",
                tags = tagsList,
                entities = emptyList(),
                actions = emptyList(),
                suggestedReminders = emptyList(),
                relatedCategories = emptyList(),
                extractedDate = null,
                extractedTime = null,
                extractedLocation = null,
                capturedAt = Instant.ofEpochMilli((data["capturedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()),
                createdAt = Instant.ofEpochMilli((data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()),
                updatedAt = Instant.ofEpochMilli((data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()),
                isSynced = true,
                isFavorite = data["isFavorite"] as? Boolean ?: false,
                isArchived = data["isArchived"] as? Boolean ?: false,
                viewCount = (data["viewCount"] as? Number)?.toInt() ?: 0,
                lastViewedAt = null
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping doc to Memory")
            null
        }
    }

    private fun mapDocumentToReminder(data: Map<String, Any>): Reminder? {
        return try {
            Reminder(
                id = data["id"] as? String ?: return null,
                memoryId = data["memoryId"] as? String ?: "",
                title = data["title"] as? String ?: "",
                description = data["description"] as? String,
                dateTime = Instant.ofEpochMilli((data["dateTime"] as? Number)?.toLong() ?: System.currentTimeMillis()),
                isCompleted = data["isCompleted"] as? Boolean ?: false,
                source = data["source"] as? String ?: "user_created",
                createdAt = Instant.ofEpochMilli((data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()),
                updatedAt = Instant.ofEpochMilli((data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis())
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping doc to Reminder")
            null
        }
    }
}
