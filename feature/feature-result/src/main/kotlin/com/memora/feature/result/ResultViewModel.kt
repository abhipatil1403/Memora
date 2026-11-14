package com.memora.feature.result

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.common.cache.ExtractedEntitiesHolder
import com.memora.core.common.reminder.LocalReminderScheduler
import com.memora.core.common.result.Result
import com.memora.core.database.dao.ReminderDao
import com.memora.core.database.entity.ReminderEntity
import com.memora.core.database.repository.MemoryRepository
import com.memora.core.model.ActionType
import com.memora.core.model.Category
import com.memora.core.model.EntityType
import com.memora.core.model.ExtractedEntity
import com.memora.core.model.Memory
import com.memora.core.model.SmartAction
import com.memora.core.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.util.UUID
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
    @ApplicationContext private val context: Context,
    private val extractedEntitiesHolder: ExtractedEntitiesHolder,
    private val memoryRepository: MemoryRepository,
    private val reminderDao: ReminderDao,
    private val localReminderScheduler: LocalReminderScheduler
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

            // 1. Derive Category dynamically
            val imageType = entities["IMAGE_TYPE"]?.firstOrNull()?.lowercase() ?: ""
            val docType = entities["DOCUMENT_TYPE"]?.firstOrNull()?.lowercase() ?: ""
            val derivedCategory = when {
                imageType.contains("receipt") || docType.contains("receipt") || entities["MERCHANT"]?.isNotEmpty() == true -> Category.RECEIPT
                imageType.contains("whiteboard") || docType.contains("whiteboard") -> Category.WHITEBOARD
                imageType.contains("notice") || docType.contains("notice") -> Category.NOTICE
                imageType.contains("prescription") || docType.contains("prescription") || entities["MEDICINE"]?.isNotEmpty() == true -> Category.PRESCRIPTION
                imageType.contains("card") || docType.contains("card") -> Category.CARD
                imageType.contains("event") || docType.contains("event") || entities["EVENT"]?.isNotEmpty() == true -> Category.EVENT
                imageType.contains("certificate") || docType.contains("certificate") -> Category.CERTIFICATE
                imageType.contains("timetable") || docType.contains("timetable") -> Category.TIMETABLE
                imageType.contains("label") || docType.contains("label") -> Category.LABEL
                else -> Category.DOCUMENT
            }

            // 2. Derive Title dynamically
            val rawTitle = entities["TITLE"]?.firstOrNull()?.trim()
            val merchantName = entities["MERCHANT"]?.firstOrNull()?.trim()
            val productName = entities["PRODUCT"]?.firstOrNull()?.trim()
            val eventName = entities["EVENT"]?.firstOrNull()?.trim()

            val derivedTitle = when {
                !rawTitle.isNullOrBlank() && !rawTitle.equals("none", ignoreCase = true) -> rawTitle
                !eventName.isNullOrBlank() -> eventName
                merchantName != null && productName != null -> "$merchantName — $productName"
                merchantName != null -> "$merchantName Receipt"
                productName != null -> productName
                imageType.isNotBlank() && !imageType.equals("photo", ignoreCase = true) -> imageType.replaceFirstChar { it.uppercase() }
                else -> "Memory ${java.time.LocalDate.now()}"
            }

            // 3. Derive Summary dynamically
            val rawSummary = entities["SUMMARY"]?.firstOrNull()?.trim()
            val derivedSummary = if (!rawSummary.isNullOrBlank() && !rawSummary.equals("none", ignoreCase = true)) {
                rawSummary
            } else {
                val details = mutableListOf<String>()
                merchantName?.let { details.add("Merchant: $it") }
                productName?.let { details.add("Product: $it") }
                entities["DATE"]?.firstOrNull()?.let { details.add("Date: $it") }
                entities["MONEY"]?.firstOrNull()?.let { details.add("Amount: $it") }
                if (details.isNotEmpty()) details.joinToString(" • ") else "Document captured and analyzed."
            }

            // 4. Derive Dynamic Tags
            val dynamicTags = mutableSetOf<String>()
            dynamicTags.add(derivedCategory.displayName.lowercase())
            if (imageType.isNotBlank() && !imageType.equals("photo", ignoreCase = true)) {
                dynamicTags.add(imageType.lowercase().replace(" ", "-"))
            }
            merchantName?.let { dynamicTags.add(it.lowercase().replace(" ", "-")) }
            entities["PRODUCT"]?.take(2)?.forEach { dynamicTags.add(it.lowercase().replace(" ", "-")) }
            entities["EVENT"]?.firstOrNull()?.let { dynamicTags.add("event") }
            val tagsList = dynamicTags.filter { it.isNotBlank() }.take(5).map { Tag(it) }

            // 5. Build Structured Entities List
            val structuredEntities = mutableListOf<ExtractedEntity>()
            fun addEntities(key: String, type: EntityType, label: String) {
                entities[key]?.forEach { valStr ->
                    if (valStr.isNotBlank()) {
                        structuredEntities.add(
                            ExtractedEntity(
                                type = type,
                                value = valStr.trim(),
                                label = label,
                                confidence = 0.92
                            )
                        )
                    }
                }
            }
            addEntities("DATE", EntityType.DATE, "Date")
            addEntities("TIME", EntityType.DATE, "Time")
            addEntities("PHONE", EntityType.CONTACT, "Phone")
            addEntities("EMAIL", EntityType.CONTACT, "Email")
            addEntities("PERSON", EntityType.CONTACT, "Person")
            addEntities("MERCHANT", EntityType.CONTACT, "Merchant")
            addEntities("LOCATION", EntityType.ADDRESS, "Location")
            addEntities("MONEY", EntityType.AMOUNT, "Amount")
            addEntities("URL", EntityType.URL, "Website")
            addEntities("MEETING", EntityType.URL, "Meeting")
            addEntities("QR_CONTENT", EntityType.QR_CODE, "QR Code")

            // 6. Build Smart Actions
            val smartActions = mutableListOf<SmartAction>()
            var actionIdx = 0
            entities["PHONE"]?.forEach { phone ->
                smartActions.add(SmartAction(ActionType.CALL, "Call $phone", phone, actionIdx++))
            }
            entities["EMAIL"]?.forEach { email ->
                smartActions.add(SmartAction(ActionType.EMAIL, "Email $email", email, actionIdx++))
            }
            entities["URL"]?.forEach { url ->
                smartActions.add(SmartAction(ActionType.OPEN_URL, "Open Link", url, actionIdx++))
            }
            entities["MEETING"]?.forEach { meetUrl ->
                smartActions.add(SmartAction(ActionType.OPEN_URL, "Join Meeting", meetUrl, actionIdx++))
            }
            entities["LOCATION"]?.forEach { loc ->
                smartActions.add(SmartAction(ActionType.OPEN_MAPS, "Open Maps ($loc)", loc, actionIdx++))
            }
            entities["REMINDER"]?.forEach { rem ->
                smartActions.add(SmartAction(ActionType.ADD_REMINDER, "Add Reminder", rem, actionIdx++))
            }
            entities["ACTION_ITEM"]?.forEach { item ->
                smartActions.add(SmartAction(ActionType.ADD_REMINDER, "Task: $item", item, actionIdx++))
            }

            // 7. Calculate Accurate Working Confidence Score & Importance
            val filledCategoriesCount = entities.count { it.value.isNotEmpty() }
            val hasHighValueKeys = entities["DATE"]?.isNotEmpty() == true ||
                    entities["MONEY"]?.isNotEmpty() == true ||
                    entities["PHONE"]?.isNotEmpty() == true ||
                    entities["EMAIL"]?.isNotEmpty() == true ||
                    entities["MEETING"]?.isNotEmpty() == true

            val calculatedConfidence = when {
                filledCategoriesCount >= 5 -> (0.92 + (filledCategoriesCount * 0.01)).coerceAtMost(0.98)
                filledCategoriesCount >= 3 -> 0.85
                filledCategoriesCount >= 1 -> 0.75
                else -> 0.50
            }

            val calculatedImportance = when {
                derivedCategory == Category.RECEIPT || derivedCategory == Category.PRESCRIPTION || hasHighValueKeys -> 0.90
                derivedCategory == Category.NOTICE || derivedCategory == Category.TIMETABLE || derivedCategory == Category.CARD -> 0.80
                else -> 0.65
            }

            val memoryId = "mem_${System.currentTimeMillis()}"

            val newMemory = Memory(
                id = memoryId,
                imageUrl = uri ?: "",
                thumbnailUrl = uri ?: "",
                title = derivedTitle,
                summary = derivedSummary,
                category = derivedCategory,
                importance = calculatedImportance,
                overallConfidence = calculatedConfidence,
                detectedLanguage = "en",
                tags = tagsList,
                entities = structuredEntities,
                actions = smartActions,
                suggestedReminders = emptyList(),
                relatedCategories = listOf(derivedCategory),
                extractedDate = null,
                extractedTime = null,
                extractedLocation = entities["LOCATION"]?.firstOrNull(),
                capturedAt = now,
                createdAt = now,
                updatedAt = now,
                isSynced = false,
                isFavorite = false,
                isArchived = false,
                viewCount = 1,
                lastViewedAt = now
            )

            // 8. Auto-create Reminders if detected
            val reminderItems = (entities["REMINDER"].orEmpty() + entities["ACTION_ITEM"].orEmpty()).filter { it.isNotBlank() }
            reminderItems.take(2).forEach { remText ->
                try {
                    val remId = "rem_${UUID.randomUUID()}"
                    val triggerTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000L) // Default tomorrow
                    reminderDao.insertReminder(
                        ReminderEntity(
                            id = remId,
                            memoryId = memoryId,
                            title = remText.take(60),
                            description = "From: $derivedTitle",
                            dateTime = triggerTime,
                            isCompleted = false,
                            source = "ai_suggested",
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                    localReminderScheduler.schedule(remId, remText, derivedTitle, triggerTime)
                } catch (e: Exception) {
                    Timber.w(e, "Could not auto-create reminder")
                }
            }

            // 9. Read image bytes
            var imageBytes: ByteArray? = null
            if (uri != null) {
                try {
                    val parsedUri = Uri.parse(uri)
                    if (parsedUri.scheme == "file") {
                        val file = java.io.File(parsedUri.path!!)
                        if (file.exists()) imageBytes = file.readBytes()
                    } else {
                        context.contentResolver.openInputStream(parsedUri)?.use { stream ->
                            imageBytes = stream.readBytes()
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to read image bytes for saving memory")
                }
            }

            // 10. Save to repository (Room + Firebase)
            val savedMemory = when (val result = memoryRepository.saveMemory(newMemory, imageBytes)) {
                is Result.Success -> result.data
                else -> newMemory
            }

            _uiState.value = ResultUiState(
                isLoading = false,
                imageUri = savedMemory.imageUrl,
                extractedEntities = entities,
                memory = savedMemory
            )
        }
    }
}
