package com.memora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val summary: String,
    val category: String,
    val importance: Double,
    val overallConfidence: Double,
    val detectedLanguage: String,
    val tags: String,                 // JSON serialized List<String>
    val entities: String,             // JSON serialized List<ExtractedEntity>
    val actions: String,              // JSON serialized List<SmartAction>
    val suggestedReminders: String,   // JSON serialized List<ReminderSuggestion>
    val relatedCategories: String,    // JSON serialized List<String>
    val extractedDate: String?,
    val extractedTime: String?,
    val extractedLocation: String?,
    val capturedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean,
    val isDeleted: Boolean,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val viewCount: Int,
    val lastViewedAt: Long?
)
