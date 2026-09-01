package com.memora.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val type: String,
    val memoryId: String?,
    val isRead: Boolean,
    val createdAt: Long,
    val readAt: Long?
)
