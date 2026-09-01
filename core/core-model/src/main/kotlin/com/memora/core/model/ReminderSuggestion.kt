package com.memora.core.model

import java.time.LocalDateTime

data class ReminderSuggestion(
    val title: String,
    val dateTime: LocalDateTime,
    val accepted: Boolean = false
)
