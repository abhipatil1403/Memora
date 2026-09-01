package com.memora.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProcessImageRequest(
    val imageUrl: String,
    val detectLanguage: Boolean = true
)

@Serializable
data class SyncMemoriesRequest(
    val memories: List<MemoryDto>,
    val lastSyncTimestamp: Long
)

@Serializable
data class SyncMemoriesResponse(
    val success: Boolean,
    val serverTimestamp: Long,
    val updatedMemories: List<MemoryDto>,
    val deletedMemoryIds: List<String>
)

@Serializable
data class MemoryDto(
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val summary: String,
    val category: String,
    val importance: Double,
    val overallConfidence: Double,
    val detectedLanguage: String,
    val tags: List<String>,
    val entities: List<ExtractedEntityDto>,
    val actions: List<SmartActionDto>,
    val suggestedReminders: List<ReminderSuggestionDto>,
    val relatedCategories: List<String>,
    val extractedDate: String?,
    val extractedTime: String?,
    val extractedLocation: String?,
    val capturedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val isDeleted: Boolean
)

@Serializable
data class ExtractedEntityDto(
    val type: String,
    val value: String,
    val label: String?,
    val confidence: Double
)

@Serializable
data class SmartActionDto(
    val type: String,
    val label: String,
    val value: String,
    val entityIndex: Int
)

@Serializable
data class ReminderSuggestionDto(
    val title: String,
    val dateTime: String,
    val accepted: Boolean
)
