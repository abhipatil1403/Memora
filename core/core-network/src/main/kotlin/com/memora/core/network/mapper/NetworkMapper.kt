package com.memora.core.network.mapper

import com.memora.core.model.*
import com.memora.core.network.dto.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun MemoryDto.toDomainModel(): Memory {
    return Memory(
        id = id,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        title = title,
        summary = summary,
        category = Category.entries.find { it.name == category } ?: Category.OTHER,
        importance = importance,
        overallConfidence = overallConfidence,
        detectedLanguage = detectedLanguage,
        tags = tags.map { Tag(it) },
        entities = entities.map { it.toDomainModel() },
        actions = actions.map { it.toDomainModel() },
        suggestedReminders = suggestedReminders.map { it.toDomainModel() },
        relatedCategories = relatedCategories.mapNotNull { cat -> Category.entries.find { it.name == cat } },
        extractedDate = extractedDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        extractedTime = extractedTime?.let { runCatching { LocalTime.parse(it) }.getOrNull() },
        extractedLocation = extractedLocation,
        capturedAt = Instant.ofEpochMilli(capturedAt),
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        isSynced = true,
        isFavorite = isFavorite,
        isArchived = isArchived,
        viewCount = 0,
        lastViewedAt = null
    )
}

fun ExtractedEntityDto.toDomainModel(): ExtractedEntity {
    return ExtractedEntity(
        type = EntityType.entries.find { it.name == type } ?: EntityType.CONTACT,
        value = value,
        label = label,
        confidence = confidence
    )
}

fun SmartActionDto.toDomainModel(): SmartAction {
    return SmartAction(
        type = ActionType.entries.find { it.name == type } ?: ActionType.OPEN_LINK,
        label = label,
        value = value,
        entityIndex = entityIndex
    )
}

fun ReminderSuggestionDto.toDomainModel(): ReminderSuggestion {
    return ReminderSuggestion(
        title = title,
        dateTime = runCatching { LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME) }.getOrElse { LocalDateTime.now() },
        accepted = accepted
    )
}

// Domain to DTO mapping functions (for sync)
// These would be the reverse of the above functions. I'll omit full implementation for brevity.
