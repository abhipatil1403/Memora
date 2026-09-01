package com.memora.core.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class Memory(
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val summary: String,
    val category: Category,
    val importance: Double,
    val overallConfidence: Double,
    val detectedLanguage: String,
    val tags: List<Tag>,
    val entities: List<ExtractedEntity>,
    val actions: List<SmartAction>,
    val suggestedReminders: List<ReminderSuggestion>,
    val relatedCategories: List<Category>,
    val extractedDate: LocalDate?,
    val extractedTime: LocalTime?,
    val extractedLocation: String?,
    val capturedAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isSynced: Boolean,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val viewCount: Int,
    val lastViewedAt: Instant?
)
