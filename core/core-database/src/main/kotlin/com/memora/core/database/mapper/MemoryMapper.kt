package com.memora.core.database.mapper

import com.memora.core.database.entity.MemoryEntity
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.model.Tag
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

fun MemoryEntity.toDomainModel(): Memory {
    val categoryEnum = try {
        Category.valueOf(category)
    } catch (_: Exception) {
        Category.OTHER
    }

    val tagList = tags.split(",").filter { it.isNotBlank() }.map { Tag(it.trim()) }

    return Memory(
        id = id,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        title = title,
        summary = summary,
        category = categoryEnum,
        importance = importance,
        overallConfidence = overallConfidence,
        detectedLanguage = detectedLanguage,
        tags = tagList,
        entities = emptyList(),
        actions = emptyList(),
        suggestedReminders = emptyList(),
        relatedCategories = emptyList(),
        extractedDate = extractedDate?.let { try { LocalDate.parse(it) } catch (_: Exception) { null } },
        extractedTime = extractedTime?.let { try { LocalTime.parse(it) } catch (_: Exception) { null } },
        extractedLocation = extractedLocation,
        capturedAt = Instant.ofEpochMilli(capturedAt),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        isSynced = isSynced,
        isFavorite = isFavorite,
        isArchived = isArchived,
        viewCount = viewCount,
        lastViewedAt = lastViewedAt?.let { Instant.ofEpochMilli(it) }
    )
}

fun Memory.toEntity(): MemoryEntity {
    val tagString = tags.joinToString(",") { it.value }

    return MemoryEntity(
        id = id,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        title = title,
        summary = summary,
        category = category.name,
        importance = importance,
        overallConfidence = overallConfidence,
        detectedLanguage = detectedLanguage,
        tags = tagString,
        entities = "[]",
        actions = "[]",
        suggestedReminders = "[]",
        relatedCategories = "[]",
        extractedDate = extractedDate?.toString(),
        extractedTime = extractedTime?.toString(),
        extractedLocation = extractedLocation,
        capturedAt = capturedAt.toEpochMilli(),
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        isSynced = isSynced,
        isDeleted = false,
        isFavorite = isFavorite,
        isArchived = isArchived,
        viewCount = viewCount,
        lastViewedAt = lastViewedAt?.toEpochMilli()
    )
}
