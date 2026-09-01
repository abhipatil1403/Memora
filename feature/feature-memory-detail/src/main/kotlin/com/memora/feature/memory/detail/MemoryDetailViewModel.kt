package com.memora.feature.memory.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class MemoryDetailUiState(
    val memory: Memory? = null,
    val versions: List<MemoryVersion> = emptyList(),
    val isLoading: Boolean = true,
    val showVersionHistory: Boolean = false
)

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // In production: inject MemoryRepository, MemoryVersionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryDetailUiState())
    val uiState: StateFlow<MemoryDetailUiState> = _uiState.asStateFlow()

    init {
        loadMemory()
    }

    fun toggleVersionHistory() {
        _uiState.update { it.copy(showVersionHistory = !it.showVersionHistory) }
    }

    fun toggleFavorite() {
        _uiState.update { state ->
            state.memory?.let {
                state.copy(memory = it.copy(isFavorite = !it.isFavorite))
            } ?: state
        }
    }

    private fun loadMemory() {
        viewModelScope.launch {
            delay(400)
            val now = Instant.now()
            _uiState.value = MemoryDetailUiState(
                isLoading = false,
                memory = Memory(
                    id = "detail_1",
                    imageUrl = "",
                    thumbnailUrl = "",
                    title = "Machine Learning Lecture Notes",
                    summary = "Prof. Kumar covered Neural Networks including backpropagation, gradient descent, and activation functions. Assignment 3 due on August 15.",
                    category = Category.WHITEBOARD,
                    importance = 0.92,
                    overallConfidence = 0.94,
                    detectedLanguage = "en",
                    tags = listOf(Tag("ML"), Tag("lecture"), Tag("neural-networks"), Tag("assignment")),
                    entities = listOf(
                        ExtractedEntity(EntityType.DATE, "August 15, 2026", "Assignment Due", 0.97),
                        ExtractedEntity(EntityType.CONTACT, "Prof. Kumar", "Professor", 0.91),
                        ExtractedEntity(EntityType.URL, "https://classroom.google.com/c/ML2026", "Classroom Link", 0.78),
                        ExtractedEntity(EntityType.ADDRESS, "AB-1 Room 302", "Lecture Hall", 0.85)
                    ),
                    actions = listOf(
                        SmartAction(ActionType.ADD_REMINDER, "Set reminder for assignment", "2026-08-15", 0),
                        SmartAction(ActionType.OPEN_URL, "Open Google Classroom", "https://classroom.google.com/c/ML2026", 2),
                        SmartAction(ActionType.OPEN_MAPS, "Open in Maps", "AB-1 Room 302", 3)
                    ),
                    suggestedReminders = listOf(
                        ReminderSuggestion("Assignment 3 Due", LocalDateTime.of(2026, 8, 15, 9, 0))
                    ),
                    relatedCategories = listOf(Category.DOCUMENT),
                    extractedDate = LocalDate.of(2026, 8, 6),
                    extractedTime = null,
                    extractedLocation = "AB-1 Room 302",
                    capturedAt = now,
                    createdAt = now,
                    updatedAt = now,
                    isSynced = true,
                    isFavorite = true,
                    isArchived = false,
                    viewCount = 5,
                    lastViewedAt = now
                ),
                versions = listOf(
                    MemoryVersion(
                        id = "v2",
                        editedAt = now,
                        editedBy = "user",
                        fields = VersionSnapshot(
                            title = "Machine Learning Lecture Notes",
                            summary = "Prof. Kumar covered Neural Networks including backpropagation, gradient descent, and activation functions. Assignment 3 due on August 15.",
                            category = "WHITEBOARD",
                            tags = listOf("ML", "lecture", "neural-networks", "assignment"),
                            entities = emptyList()
                        ),
                        changeDescription = "Updated assignment due date"
                    ),
                    MemoryVersion(
                        id = "v1",
                        editedAt = Instant.ofEpochMilli(now.toEpochMilli() - 3600000),
                        editedBy = "ai",
                        fields = VersionSnapshot(
                            title = "ML Lecture",
                            summary = "Prof Kumar neural networks backpropagation",
                            category = "WHITEBOARD",
                            tags = listOf("ML"),
                            entities = emptyList()
                        ),
                        changeDescription = "Initial AI extraction"
                    )
                )
            )
        }
    }
}
