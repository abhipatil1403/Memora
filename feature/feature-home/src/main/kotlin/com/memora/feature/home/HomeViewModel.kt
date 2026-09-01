package com.memora.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.model.SmartCollection
import com.memora.core.model.CollectionType
import com.memora.core.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "Good morning",
    val recentMemories: List<Memory> = emptyList(),
    val collections: List<SmartCollection> = emptyList(),
    val isLoading: Boolean = true,
    val totalMemories: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    // In production: inject MemoryRepository, CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Simulate data load — in production, this observes Room Flows
            delay(800)
            _uiState.value = HomeUiState(
                greeting = getTimeBasedGreeting(),
                recentMemories = generateSampleMemories(),
                collections = generateSampleCollections(),
                isLoading = false,
                totalMemories = 47
            )
        }
    }

    private fun getTimeBasedGreeting(): String {
        val hour = java.time.LocalTime.now().hour
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun generateSampleMemories(): List<Memory> {
        val now = Instant.now()
        return listOf(
            Memory(
                id = "1", imageUrl = "", thumbnailUrl = "",
                title = "Machine Learning Lecture Notes",
                summary = "Prof. Kumar — Neural Networks, Backpropagation, Gradient Descent",
                category = Category.WHITEBOARD,
                importance = 0.92, overallConfidence = 0.95,
                detectedLanguage = "en", tags = listOf(Tag("ML"), Tag("lecture")),
                entities = emptyList(), actions = emptyList(),
                suggestedReminders = emptyList(), relatedCategories = emptyList(),
                extractedDate = null, extractedTime = null, extractedLocation = null,
                capturedAt = now, createdAt = now, updatedAt = now,
                isSynced = true, isFavorite = true, isArchived = false,
                viewCount = 5, lastViewedAt = now
            ),
            Memory(
                id = "2", imageUrl = "", thumbnailUrl = "",
                title = "Pharmacy Receipt — Apollo",
                summary = "₹1,240 — Paracetamol, Vitamin D3, Blood Pressure Monitor",
                category = Category.RECEIPT,
                importance = 0.7, overallConfidence = 0.88,
                detectedLanguage = "en", tags = listOf(Tag("medical"), Tag("expense")),
                entities = emptyList(), actions = emptyList(),
                suggestedReminders = emptyList(), relatedCategories = emptyList(),
                extractedDate = null, extractedTime = null, extractedLocation = null,
                capturedAt = now, createdAt = now, updatedAt = now,
                isSynced = true, isFavorite = false, isArchived = false,
                viewCount = 2, lastViewedAt = now
            ),
            Memory(
                id = "3", imageUrl = "", thumbnailUrl = "",
                title = "Hackathon Poster — TechFest 2026",
                summary = "Registration deadline: Aug 15. Venue: AB-1 Hall. Prize: ₹50,000",
                category = Category.EVENT,
                importance = 0.85, overallConfidence = 0.91,
                detectedLanguage = "en", tags = listOf(Tag("event"), Tag("college")),
                entities = emptyList(), actions = emptyList(),
                suggestedReminders = emptyList(), relatedCategories = emptyList(),
                extractedDate = null, extractedTime = null, extractedLocation = null,
                capturedAt = now, createdAt = now, updatedAt = now,
                isSynced = true, isFavorite = false, isArchived = false,
                viewCount = 1, lastViewedAt = now
            )
        )
    }

    private fun generateSampleCollections(): List<SmartCollection> {
        return listOf(
            SmartCollection(CollectionType.TODAY, "Today", 3, "calendar_today"),
            SmartCollection(CollectionType.THIS_WEEK, "This Week", 12, "date_range"),
            SmartCollection(CollectionType.COLLEGE, "College", 18, "school"),
            SmartCollection(CollectionType.RECEIPTS, "Receipts", 8, "receipt_long"),
            SmartCollection(CollectionType.IMPORTANT, "Important", 5, "star"),
            SmartCollection(CollectionType.FAVORITES, "Favorites", 4, "favorite")
        )
    }
}
