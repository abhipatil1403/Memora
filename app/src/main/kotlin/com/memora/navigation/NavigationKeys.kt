package com.memora.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// ── Core navigation destinations ──────────────────────────────────
@Serializable data object SplashKey : NavKey
@Serializable data object OnboardingKey : NavKey
@Serializable data object AuthKey : NavKey
@Serializable data object HomeKey : NavKey
@Serializable data object LibraryKey : NavKey
@Serializable data object CaptureKey : NavKey
@Serializable data class ProcessingKey(val imageId: String) : NavKey
@Serializable data class ResultKey(val memoryId: String) : NavKey
@Serializable data class MemoryDetailKey(val memoryId: String) : NavKey
@Serializable data object SearchKey : NavKey
@Serializable data object CollectionsKey : NavKey
@Serializable data object RemindersKey : NavKey
@Serializable data object ProfileKey : NavKey
@Serializable data object SettingsKey : NavKey
