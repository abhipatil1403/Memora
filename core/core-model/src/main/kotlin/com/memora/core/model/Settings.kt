package com.memora.core.model

data class UserPreferences(
    val darkMode: DarkMode,
    val notificationsEnabled: Boolean,
    val language: String,
    val backupEnabled: Boolean
)

enum class DarkMode {
    SYSTEM,
    LIGHT,
    DARK
}
