package com.memora.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = listOf("assignment due date", "pharmacy receipt", "Prof. Kumar", "hackathon"),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.length >= 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(300) // debounce
                performSearch(query)
            }
        } else {
            _uiState.update { it.copy(results = emptyList(), hasSearched = false) }
        }
    }

    fun searchFromRecent(query: String) {
        _uiState.update { it.copy(query = query) }
        viewModelScope.launch { performSearch(query) }
    }

    fun clearSearch() {
        _uiState.update { it.copy(query = "", results = emptyList(), hasSearched = false) }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true) }
        delay(500) // simulate network
        val now = Instant.now()
        val results = getSampleMemories(now)
            .filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) }
            .mapIndexed { i, m -> SearchResult(m, 0.95 - (i * 0.1), "title") }
        _uiState.update { it.copy(results = results, isSearching = false, hasSearched = true) }
    }

    private fun getSampleMemories(now: Instant) = listOf(
        createMemory("1", "ML Lecture Notes", "Backpropagation, Gradient Descent, Prof. Kumar", Category.WHITEBOARD, now),
        createMemory("2", "Pharmacy Receipt", "₹1,240 — Paracetamol, Vitamin D3", Category.RECEIPT, now),
        createMemory("3", "Hackathon Poster", "TechFest 2026, Prize: ₹50,000, deadline Aug 15", Category.EVENT, now),
        createMemory("4", "Dr. Sharma Prescription", "Blood pressure medication, follow-up Aug 20", Category.PRESCRIPTION, now),
        createMemory("5", "Business Card — Raj Patel", "CTO at StartHub, raj@starthub.io", Category.CARD, now),
    )

    private fun createMemory(id: String, title: String, summary: String, category: Category, now: Instant) = Memory(
        id = id, imageUrl = "", thumbnailUrl = "", title = title, summary = summary,
        category = category, importance = 0.8, overallConfidence = 0.9,
        detectedLanguage = "en", tags = emptyList(), entities = emptyList(),
        actions = emptyList(), suggestedReminders = emptyList(), relatedCategories = emptyList(),
        extractedDate = null, extractedTime = null, extractedLocation = null,
        capturedAt = now, createdAt = now, updatedAt = now,
        isSynced = true, isFavorite = false, isArchived = false, viewCount = 0, lastViewedAt = null
    )
}
