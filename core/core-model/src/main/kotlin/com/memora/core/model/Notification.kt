package com.memora.core.model

import java.time.Instant

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val memoryId: String?,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?
)

enum class NotificationType {
    REMINDER,
    SYSTEM,
    INSIGHT
}
