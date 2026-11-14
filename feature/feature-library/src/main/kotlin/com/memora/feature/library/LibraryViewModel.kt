package com.memora.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.database.repository.MemoryRepository
import com.memora.core.model.Category
import com.memora.core.model.CollectionType
import com.memora.core.model.Memory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

data class LibraryUiState(
    val memories: List<Memory> = emptyList(),
    val filteredMemories: List<Memory> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedCollectionType: CollectionType? = null,
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
                    currentState.copy(
                        memories = memories,
                        filteredMemories = computeFilteredMemories(memories, currentState),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setInitialCollection(collectionTypeName: String?) {
        val collectionType = collectionTypeName?.let { name ->
            try {
                CollectionType.valueOf(name.uppercase())
            } catch (e: Exception) {
                null
            }
        }
        _uiState.update { state ->
            val updated = state.copy(
                selectedCollectionType = collectionType,
                selectedCategory = null
            )
            updated.copy(filteredMemories = computeFilteredMemories(updated.memories, updated))
        }
    }

    fun filterByCategory(category: Category?) {
        _uiState.update { state ->
            val updated = state.copy(selectedCategory = category)
            updated.copy(filteredMemories = computeFilteredMemories(updated.memories, updated))
        }
    }

    fun clearCollectionFilter() {
        _uiState.update { state ->
            val updated = state.copy(selectedCollectionType = null)
            updated.copy(filteredMemories = computeFilteredMemories(updated.memories, updated))
        }
    }

    fun search(query: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = query)
            updated.copy(filteredMemories = computeFilteredMemories(updated.memories, updated))
        }
    }

    private fun computeFilteredMemories(memories: List<Memory>, state: LibraryUiState): List<Memory> {
        val now = ZonedDateTime.now()
        val today = now.toLocalDate().atStartOfDay(now.zone).toInstant()
        val week = now.toLocalDate().minusDays((now.dayOfWeek.value - 1).toLong()).atStartOfDay(now.zone).toInstant()

        return memories.filter { memory ->
            val matchesCollection = when (state.selectedCollectionType) {
                CollectionType.TODAY -> memory.createdAt >= today
                CollectionType.THIS_WEEK -> memory.createdAt >= week
                CollectionType.RECEIPTS -> memory.category == Category.RECEIPT
                CollectionType.DOCUMENTS -> memory.category == Category.DOCUMENT
                CollectionType.COLLEGE -> memory.category in listOf(
                    Category.WHITEBOARD,
                    Category.NOTICE,
                    Category.TIMETABLE,
                    Category.CERTIFICATE
                )
                CollectionType.BUSINESS -> memory.category == Category.CARD
                CollectionType.MEDICAL -> memory.category == Category.PRESCRIPTION ||
                        memory.title.contains("medical", ignoreCase = true) ||
                        memory.title.contains("prescription", ignoreCase = true) ||
                        memory.summary.contains("medicine", ignoreCase = true) ||
                        memory.summary.contains("doctor", ignoreCase = true) ||
                        memory.summary.contains("hospital", ignoreCase = true) ||
                        memory.tags.any { tag -> tag.value.contains("medic", ignoreCase = true) || tag.value.contains("health", ignoreCase = true) }
                CollectionType.IMPORTANT -> memory.importance >= 0.8
                CollectionType.FAVORITES -> memory.isFavorite
                CollectionType.ARCHIVED -> memory.isArchived
                null -> true
            }

            val matchesCategory = if (state.selectedCategory == null) true else memory.category == state.selectedCategory

            val matchesQuery = if (state.searchQuery.isBlank()) true else {
                memory.title.contains(state.searchQuery, ignoreCase = true) ||
                        memory.summary.contains(state.searchQuery, ignoreCase = true) ||
                        memory.tags.any { it.value.contains(state.searchQuery, ignoreCase = true) }
            }

            matchesCollection && matchesCategory && matchesQuery
        }
    }
}
