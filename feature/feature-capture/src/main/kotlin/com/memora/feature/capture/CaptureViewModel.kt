package com.memora.feature.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaptureUiState(
    val isCameraReady: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val isFrontCamera: Boolean = false,
    val capturedImageUri: String? = null,
    val isProcessing: Boolean = false,
    val error: String? = null
)

sealed class CaptureEvent {
    data object ToggleFlash : CaptureEvent()
    data object ToggleCamera : CaptureEvent()
    data object CameraReady : CaptureEvent()
    data class PhotoCaptured(val uri: String) : CaptureEvent()
    data object RetakePhoto : CaptureEvent()
    data object ConfirmPhoto : CaptureEvent()
    data object DismissError : CaptureEvent()
}

@HiltViewModel
class CaptureViewModel @Inject constructor(
    // In production: inject ImageUploadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CaptureUiState())
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    // Will be set after confirm
    private val _navigateToProcessing = MutableStateFlow<String?>(null)
    val navigateToProcessing: StateFlow<String?> = _navigateToProcessing.asStateFlow()

    fun onEvent(event: CaptureEvent) {
        when (event) {
            CaptureEvent.ToggleFlash -> _uiState.update { it.copy(isFlashEnabled = !it.isFlashEnabled) }
            CaptureEvent.ToggleCamera -> _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
            CaptureEvent.CameraReady -> _uiState.update { it.copy(isCameraReady = true) }
            is CaptureEvent.PhotoCaptured -> _uiState.update { it.copy(capturedImageUri = event.uri) }
            CaptureEvent.RetakePhoto -> _uiState.update { it.copy(capturedImageUri = null) }
            CaptureEvent.ConfirmPhoto -> confirmAndUpload()
            CaptureEvent.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun confirmAndUpload() {
        val uri = _uiState.value.capturedImageUri ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }
            kotlinx.coroutines.delay(500)
            _uiState.update { it.copy(isProcessing = false) }
            // Pass the actual image URI so ProcessingViewModel can read the image bytes
            _navigateToProcessing.value = uri
        }
    }
}
