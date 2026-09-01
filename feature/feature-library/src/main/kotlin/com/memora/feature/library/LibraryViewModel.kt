package com.memora.feature.library

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
import javax.inject.Inject

data class LibraryUiState(
    val memories: List<Memory> = emptyList(),
    val filteredMemories: List<Memory> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    // In production: inject MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        viewModelScope.launch {
            delay(500)
            val now = Instant.now()
            val memories = listOf(
                createMemory("1", "ML Lecture Notes", "Backpropagation, Gradient Descent", Category.WHITEBOARD, 0.94, now),
                createMemory("2", "Pharmacy Receipt", "₹1,240 — Paracetamol, Vitamin D3", Category.RECEIPT, 0.88, now),
                createMemory("3", "Hackathon Poster", "TechFest 2026, Prize: ₹50,000", Category.EVENT, 0.91, now),
                createMemory("4", "Dr. Sharma Prescription", "Blood pressure medication, follow-up Aug 20", Category.PRESCRIPTION, 0.85, now),
                createMemory("5", "Business Card — Startup Hub", "Raj Patel, CTO, raj@starthub.io", Category.CARD, 0.96, now),
                createMemory("6", "Semester Timetable", "Mon-Fri schedule, Room assignments", Category.TIMETABLE, 0.92, now),
                createMemory("7", "Library Notice", "Book return deadline: Aug 10", Category.NOTICE, 0.89, now),
                createMemory("8", "Grocery Bill", "₹2,340 — Reliance Smart", Category.RECEIPT, 0.93, now)
            )
            _uiState.update { it.copy(memories = memories, filteredMemories = memories, isLoading = false) }
        }
    }

    fun filterByCategory(category: Category?) {
        _uiState.update { state ->
            val filtered = if (category == null) state.memories
            else state.memories.filter { it.category == category }
            state.copy(selectedCategory = category, filteredMemories = filtered)
        }
    }

    fun search(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.memories
            else state.memories.filter {
                it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredMemories = filtered)
        }
    }

    private fun createMemory(
        id: String, title: String, summary: String, category: Category, confidence: Double, now: Instant
    ): Memory = Memory(
        id = id, imageUrl = "", thumbnailUrl = "", title = title, summary = summary,
        category = category, importance = 0.8, overallConfidence = confidence,
        detectedLanguage = "en", tags = emptyList(), entities = emptyList(),
        actions = emptyList(), suggestedReminders = emptyList(), relatedCategories = emptyList(),
        extractedDate = null, extractedTime = null, extractedLocation = null,
        capturedAt = now, createdAt = now, updatedAt = now,
        isSynced = true, isFavorite = false, isArchived = false,
        viewCount = 0, lastViewedAt = null
    )
}
