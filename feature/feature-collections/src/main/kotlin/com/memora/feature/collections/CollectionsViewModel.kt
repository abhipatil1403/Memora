package com.memora.feature.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.database.repository.MemoryRepository
import com.memora.core.model.Category
import com.memora.core.model.CollectionType
import com.memora.core.model.SmartCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

data class CollectionsUiState(
    val collections: List<SmartCollection> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            memoryRepository.getAllMemories().collect { memories ->
                val now = ZonedDateTime.now()
                val today = now.toLocalDate().atStartOfDay(now.zone).toInstant()
                val week = now.toLocalDate().minusDays((now.dayOfWeek.value - 1).toLong()).atStartOfDay(now.zone).toInstant()

                _uiState.value = CollectionsUiState(
                    isLoading = false,
                    collections = listOf(
                        SmartCollection(CollectionType.TODAY, "Today", memories.count { it.createdAt >= today }, "calendar_today"),
                        SmartCollection(CollectionType.THIS_WEEK, "This Week", memories.count { it.createdAt >= week }, "date_range"),
                        SmartCollection(CollectionType.RECEIPTS, "Receipts", memories.count { it.category == Category.RECEIPT }, "receipt_long"),
                        SmartCollection(CollectionType.DOCUMENTS, "Documents", memories.count { it.category == Category.DOCUMENT }, "description"),
                        SmartCollection(CollectionType.COLLEGE, "College", memories.count { it.category in listOf(Category.WHITEBOARD, Category.NOTICE, Category.TIMETABLE, Category.CERTIFICATE) }, "school"),
                        SmartCollection(CollectionType.BUSINESS, "Business", memories.count { it.category == Category.CARD }, "work"),
                        SmartCollection(
                            CollectionType.MEDICAL,
                            "Medical",
                            memories.count {
                                it.category == Category.PRESCRIPTION ||
                                        it.title.contains("medical", ignoreCase = true) ||
                                        it.title.contains("prescription", ignoreCase = true) ||
                                        it.summary.contains("medicine", ignoreCase = true) ||
                                        it.summary.contains("doctor", ignoreCase = true) ||
                                        it.summary.contains("hospital", ignoreCase = true) ||
                                        it.tags.any { tag -> tag.value.contains("medic", ignoreCase = true) || tag.value.contains("health", ignoreCase = true) }
                            },
                            "local_hospital"
                        ),
                        SmartCollection(CollectionType.IMPORTANT, "Important", memories.count { it.importance >= 0.8 }, "star"),
                        SmartCollection(CollectionType.FAVORITES, "Favorites", memories.count { it.isFavorite }, "favorite")
                    )
                )
            }
        }
    }
}
