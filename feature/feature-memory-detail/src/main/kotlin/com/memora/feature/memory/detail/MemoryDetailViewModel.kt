package com.memora.feature.memory.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryDetailUiState(
    val memory: Memory? = null,
    val versions: List<MemoryVersion> = emptyList(),
    val isLoading: Boolean = true,
    val showVersionHistory: Boolean = false
)

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    private val memoryRepository: com.memora.core.database.repository.MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryDetailUiState())
    val uiState: StateFlow<MemoryDetailUiState> = _uiState.asStateFlow()

    fun toggleVersionHistory() {
        _uiState.update { it.copy(showVersionHistory = !it.showVersionHistory) }
    }

    fun toggleFavorite() {
        val id = _uiState.value.memory?.id ?: return
        viewModelScope.launch { memoryRepository.toggleFavorite(id) }
    }

    fun loadMemory(memoryId: String) {
        _uiState.value = MemoryDetailUiState(memory = null, isLoading = true)
        viewModelScope.launch {
            memoryRepository.getMemoryById(memoryId).collect { memory ->
                _uiState.value = MemoryDetailUiState(memory = memory, isLoading = false)
            }
        }
    }
}
