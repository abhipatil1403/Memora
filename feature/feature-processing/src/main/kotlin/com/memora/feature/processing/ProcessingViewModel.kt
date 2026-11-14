package com.memora.feature.processing

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.common.cache.ExtractedEntitiesHolder
import com.memora.core.network.api.MemoraApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject

data class ProcessingStep(
    val label: String,
    val isComplete: Boolean = false,
    val isActive: Boolean = false
)

data class ProcessingUiState(
    val steps: List<ProcessingStep> = listOf(
        ProcessingStep("Uploading image"),
        ProcessingStep("Analyzing image..."),
        ProcessingStep("Extracting entities"),
        ProcessingStep("Formatting results")
    ),
    val progress: Float = 0f,
    val isComplete: Boolean = false,
    val resultMemoryId: String? = null,
    val error: String? = null
)

@HiltViewModel
class ProcessingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val memoraApi: MemoraApi,
    private val extractedEntitiesHolder: ExtractedEntitiesHolder
) : ViewModel() {

    private var currentImageUriString: String? = null
    private var processingJob: Job? = null

    private val _uiState = MutableStateFlow(ProcessingUiState())
    val uiState: StateFlow<ProcessingUiState> = _uiState.asStateFlow()

    fun resetState() {
        processingJob?.cancel()
        processingJob = null
        currentImageUriString = null
        _uiState.value = ProcessingUiState(
            steps = listOf(
                ProcessingStep("Uploading image"),
                ProcessingStep("Analyzing image..."),
                ProcessingStep("Extracting entities"),
                ProcessingStep("Formatting results")
            ),
            progress = 0f,
            isComplete = false,
            resultMemoryId = null,
            error = null
        )
    }

    fun startProcessing(imageUriString: String) {
        processingJob?.cancel()
        currentImageUriString = imageUriString
        Timber.d("ProcessingViewModel startProcessing — imageUriString = %s", imageUriString)

        // Clear cached entities from any previous run immediately
        extractedEntitiesHolder.clear()

        // Reset UI state to fresh processing mode
        val initialSteps = listOf(
            ProcessingStep("Uploading image", isActive = true),
            ProcessingStep("Analyzing image..."),
            ProcessingStep("Extracting entities"),
            ProcessingStep("Formatting results")
        )
        _uiState.value = ProcessingUiState(
            steps = initialSteps,
            progress = 0.15f,
            isComplete = false,
            resultMemoryId = null,
            error = null
        )

        processingJob = viewModelScope.launch {
            val steps = _uiState.value.steps.toMutableList()

            // Step 0: Uploading image
            steps[0] = steps[0].copy(isActive = true)
            _uiState.update { it.copy(steps = steps.toList(), progress = 0.25f) }

            val imageBytes = readImageBytes(imageUriString)
            Timber.d("Image bytes read: %s bytes (uri=%s)", imageBytes?.size ?: "NULL", imageUriString)

            if (imageBytes == null || imageBytes.isEmpty()) {
                Timber.e("CRITICAL: Could not read image bytes from URI: %s", imageUriString)
                _uiState.update { it.copy(error = "Failed to read image. URI: $imageUriString") }
                // Still continue but log clearly that we're sending garbage
            }

            val requestBody = (imageBytes ?: createFallbackDummyImageBytes())
                .toRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", "uploaded_image.jpg", requestBody)

            steps[0] = steps[0].copy(isActive = false, isComplete = true)

            // Step 1: Analyzing image...
            steps[1] = steps[1].copy(isActive = true)
            _uiState.update { it.copy(steps = steps.toList(), progress = 0.50f) }

            var extractedEntities: Map<String, List<String>>? = null

            try {
                Timber.d("Calling memoraApi.extractEntities...")
                val response = memoraApi.extractEntities(filePart)
                Timber.d("API response code: %d, successful: %b", response.code(), response.isSuccessful)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.success) {
                        Timber.d("API returned success=%b, entities=%s", body.success, body.entities)
                        extractedEntities = body.entities
                    } else {
                        val backendError = body.error ?: "The server processed the request but returned success=false."
                        _uiState.update { it.copy(error = "Backend Error: $backendError") }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Timber.e("API error response: code=%d, errorBody=%s", response.code(), errorBody)
                    _uiState.update { it.copy(error = "Network Error: HTTP ${response.code()}. $errorBody") }
                }
            } catch (e: Exception) {
                Timber.e(e, "Backend API call failed with exception")
                _uiState.update { it.copy(error = "Connection Error: ${e.message}") }
            }

            if (_uiState.value.error != null || extractedEntities == null) {
                // If there's an error, stop processing here. The UI will show the error state.
                steps[1] = steps[1].copy(isActive = false, isComplete = false)
                _uiState.update { it.copy(steps = steps.toList()) }
                return@launch
            }

            steps[1] = steps[1].copy(isActive = false, isComplete = true)

            // Step 2: Extracting entities
            steps[2] = steps[2].copy(isActive = true)
            _uiState.update { it.copy(steps = steps.toList(), progress = 0.75f) }
            extractedEntitiesHolder.setExtractedEntities(imageUriString, extractedEntities)
            steps[2] = steps[2].copy(isActive = false, isComplete = true)

            // Step 3: Formatting results
            steps[3] = steps[3].copy(isActive = false, isComplete = true)
            _uiState.update {
                it.copy(
                    steps = steps.toList(),
                    progress = 1.0f,
                    isComplete = true,
                    resultMemoryId = "mem_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}"
                )
            }
        }
    }

    private fun readImageBytes(uriString: String?): ByteArray? {
        if (uriString.isNullOrBlank()) {
            Timber.e("readImageBytes: uriString is null or blank")
            return null
        }
        return try {
            val uri = Uri.parse(uriString)
            Timber.d("readImageBytes: parsed URI = %s, scheme = %s", uri, uri.scheme)
            
            if (uri.scheme == "file") {
                // Read directly from file
                val file = java.io.File(uri.path!!)
                if (file.exists()) {
                    val bytes = file.readBytes()
                    Timber.d("readImageBytes: successfully read %d bytes from file", bytes.size)
                    bytes
                } else {
                    Timber.e("readImageBytes: file does not exist at path %s", uri.path)
                    null
                }
            } else {
                // Content resolver for content://
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    Timber.d("readImageBytes: successfully read %d bytes from content resolver", bytes.size)
                    bytes
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reading image bytes from URI: %s", uriString)
            null
        }
    }

    private fun createFallbackEntities(): Map<String, List<String>> {
        return mapOf(
            "MERCHANT" to listOf("McDonald's"),
            "PRODUCT" to listOf("MAC"),
            "DATE" to listOf("12-May-2021"),
            "PHONE" to listOf("9876543210")
        )
    }

    private fun createFallbackDummyImageBytes(): ByteArray {
        return byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00.toByte(), 0x10.toByte(),
            0x4A.toByte(), 0x46.toByte(), 0x49.toByte(), 0x46.toByte(), 0x00.toByte(), 0x01.toByte(),
            0x01.toByte(), 0x01.toByte(), 0x00.toByte(), 0x48.toByte(), 0x00.toByte(), 0x48.toByte(),
            0x00.toByte(), 0x00.toByte(), 0xFF.toByte(), 0xDB.toByte(), 0x00.toByte(), 0x43.toByte(),
            0x00.toByte(), 0xFF.toByte(), 0xC0.toByte(), 0x00.toByte(), 0x0B.toByte(), 0x08.toByte(),
            0x00.toByte(), 0x01.toByte(), 0x00.toByte(), 0x01.toByte(), 0x01.toByte(), 0x01.toByte(),
            0x11.toByte(), 0x00.toByte(), 0xFF.toByte(), 0xC4.toByte(), 0x00.toByte(), 0x1F.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x05.toByte(), 0x01.toByte(), 0x01.toByte(),
            0x01.toByte(), 0x01.toByte(), 0x01.toByte(), 0x01.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x01.toByte(), 0x02.toByte(), 0x03.toByte(), 0x04.toByte(), 0x05.toByte(), 0x06.toByte(),
            0x07.toByte(), 0x08.toByte(), 0x09.toByte(), 0x0A.toByte(), 0x0B.toByte(), 0xFF.toByte(),
            0xDA.toByte(), 0x00.toByte(), 0x08.toByte(), 0x01.toByte(), 0x01.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x3F.toByte(), 0x00.toByte(), 0xBF.toByte(), 0x00.toByte(), 0xFF.toByte(),
            0xD9.toByte()
        )
    }
}
