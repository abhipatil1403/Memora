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
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import com.memora.core.database.repository.MemoryRepository

data class LibraryUiState(
    val memories: List<Memory> = emptyList(),
    val filteredMemories: List<Memory> = emptyList(),
    val selectedCategory: Category? = null,
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        viewModelScope.launch {
            memoryRepository.getAllMemories().collect { memories ->
                _uiState.update { currentState ->
                    val query = currentState.searchQuery
                    val category = currentState.selectedCategory
                    
                    val filtered = memories.filter { memory ->
                        val matchesQuery = if (query.isBlank()) true else {
                            memory.title.contains(query, ignoreCase = true) || memory.summary.contains(query, ignoreCase = true)
                        }
                        val matchesCategory = if (category == null) true else memory.category == category
                        matchesQuery && matchesCategory
                    }
                    
                    currentState.copy(
                        memories = memories,
                        filteredMemories = filtered,
                        isLoading = false
                    )
                }
            }
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


}
