package com.memora.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.model.SmartCollection
import com.memora.core.model.CollectionType
import com.memora.core.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import com.memora.core.database.repository.MemoryRepository

data class HomeUiState(
    val greeting: String = "Good morning",
    val recentMemories: List<Memory> = emptyList(),
    val collections: List<SmartCollection> = emptyList(),
    val isLoading: Boolean = true,
    val totalMemories: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            memoryRepository.getRecentMemories(10).collect { memories ->
                _uiState.update { currentState ->
                    currentState.copy(
                        greeting = getTimeBasedGreeting(),
                        recentMemories = memories,
                        collections = buildCollections(memories),
                        isLoading = false,
                        totalMemories = memories.size // Or fetch total separately if needed
                    )
                }
            }
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

    private fun buildCollections(memories: List<Memory>): List<SmartCollection> {
        val now = java.time.ZonedDateTime.now()
        val today = now.toLocalDate().atStartOfDay(now.zone).toInstant()
        val week = now.toLocalDate().minusDays((now.dayOfWeek.value - 1).toLong()).atStartOfDay(now.zone).toInstant()
        fun count(vararg categories: Category) = memories.count { it.category in categories }
        return listOf(
            SmartCollection(CollectionType.TODAY, "Today", memories.count { it.createdAt >= today }, "calendar_today"),
            SmartCollection(CollectionType.THIS_WEEK, "This Week", memories.count { it.createdAt >= week }, "date_range"),
            SmartCollection(CollectionType.COLLEGE, "College", count(Category.WHITEBOARD, Category.NOTICE, Category.TIMETABLE, Category.CERTIFICATE), "school"),
            SmartCollection(CollectionType.RECEIPTS, "Receipts", count(Category.RECEIPT), "receipt_long"),
            SmartCollection(CollectionType.IMPORTANT, "Important", memories.count { it.importance >= 0.8 }, "star"),
            SmartCollection(CollectionType.FAVORITES, "Favorites", memories.count { it.isFavorite }, "favorite")
        )
    }
}
