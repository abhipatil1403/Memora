package com.memora.feature.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.common.cache.ExtractedEntitiesHolder
import com.memora.core.model.Category
import com.memora.core.model.Memory
import com.memora.core.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class ResultUiState(
    val memory: Memory? = null,
    val imageUri: String? = null,
    val extractedEntities: Map<String, List<String>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val extractedEntitiesHolder: ExtractedEntitiesHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        loadResult()
    }

    private fun loadResult() {
        viewModelScope.launch {
            val entities = extractedEntitiesHolder.getExtractedEntities()
            val uri = extractedEntitiesHolder.getLastImageUri()
            val now = Instant.now()

            // Derive title/summary from extracted entities if available
            val merchantName = entities["MERCHANT"]?.firstOrNull()
            val productName = entities["PRODUCT"]?.firstOrNull()
            val derivedTitle = when {
                merchantName != null && productName != null -> "$merchantName - $productName"
                merchantName != null -> "$merchantName Receipt / Capture"
                productName != null -> "$productName Document"
                else -> "AI Analyzed Capture"
            }

            val derivedCategory = when {
                entities["MERCHANT"]?.isNotEmpty() == true -> Category.RECEIPT
                entities["PRODUCT"]?.isNotEmpty() == true -> Category.LABEL
                entities["MEDICINE"]?.isNotEmpty() == true -> Category.PRESCRIPTION
                else -> Category.DOCUMENT
            }

            val dummyMemory = Memory(
                id = "result_${System.currentTimeMillis()}",
                imageUrl = uri ?: "",
                thumbnailUrl = uri ?: "",
                title = derivedTitle,
                summary = "AI Extracted information from your document",
                category = derivedCategory,
                importance = 0.90,
                overallConfidence = 0.95,
                detectedLanguage = "en",
                tags = listOf(Tag("ai-extracted"), Tag(derivedCategory.name.lowercase())),
                entities = emptyList(),
                actions = emptyList(),
                suggestedReminders = emptyList(),
                relatedCategories = emptyList(),
                extractedDate = null,
                extractedTime = null,
                extractedLocation = null,
                capturedAt = now,
                createdAt = now,
                updatedAt = now,
                isSynced = true,
                isFavorite = false,
                isArchived = false,
                viewCount = 1,
                lastViewedAt = now
            )

            _uiState.value = ResultUiState(
                isLoading = false,
                imageUri = uri,
                extractedEntities = entities,
                memory = dummyMemory
            )
        }
    }
}
