package com.memora.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.database.repository.MemoryRepository
import com.memora.core.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

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
        val results = memoryRepository.getAllMemories()
            .first()
            .filter { memory ->
                memory.title.contains(query, ignoreCase = true) ||
                    memory.summary.contains(query, ignoreCase = true) ||
                    memory.tags.any { it.value.contains(query, ignoreCase = true) }
            }
            .mapIndexed { i, m -> SearchResult(m, 0.95 - (i * 0.1), "title") }
        _uiState.update { it.copy(results = results, isSearching = false, hasSearched = true) }
    }
}
