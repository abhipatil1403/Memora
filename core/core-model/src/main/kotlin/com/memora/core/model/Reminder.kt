package com.memora.core.model

import java.time.Instant

data class Reminder(
    val id: String,
    val memoryId: String,
    val title: String,
    val description: String?,
    val dateTime: Instant,
    val isCompleted: Boolean,
    val source: String, // "ai_suggested" | "user_created"
    val createdAt: Instant,
    val updatedAt: Instant
)
