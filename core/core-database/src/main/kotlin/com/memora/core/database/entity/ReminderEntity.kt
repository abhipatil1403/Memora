package com.memora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val memoryId: String,
    val title: String,
    val description: String?,
    val dateTime: Long,
    val isCompleted: Boolean,
    val source: String,               // "ai_suggested" | "user_created"
    val createdAt: Long,
    val updatedAt: Long
)
