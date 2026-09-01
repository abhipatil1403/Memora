package com.memora.feature.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CollectionsUiState(
    val collections: List<SmartCollection> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CollectionsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionsUiState())
    val uiState: StateFlow<CollectionsUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            delay(400)
            _uiState.value = CollectionsUiState(
                isLoading = false,
                collections = listOf(
                    SmartCollection(CollectionType.TODAY, "Today", 3, "calendar_today"),
                    SmartCollection(CollectionType.THIS_WEEK, "This Week", 12, "date_range"),
                    SmartCollection(CollectionType.COLLEGE, "College", 18, "school"),
                    SmartCollection(CollectionType.DOCUMENTS, "Documents", 7, "description"),
                    SmartCollection(CollectionType.RECEIPTS, "Receipts", 8, "receipt_long"),
                    SmartCollection(CollectionType.BUSINESS, "Business", 4, "work"),
                    SmartCollection(CollectionType.MEDICAL, "Medical", 3, "local_hospital"),
                    SmartCollection(CollectionType.IMPORTANT, "Important", 5, "star"),
                    SmartCollection(CollectionType.FAVORITES, "Favorites", 4, "favorite"),
                    SmartCollection(CollectionType.ARCHIVED, "Archived", 2, "archive")
                )
            )
        }
    }
}
