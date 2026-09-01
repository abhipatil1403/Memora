package com.memora.feature.settings

import androidx.lifecycle.ViewModel
import com.memora.core.model.DarkMode
import com.memora.core.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(
        darkMode = DarkMode.LIGHT,
        notificationsEnabled = true,
        language = "en",
        backupEnabled = false
    ),
    val appVersion: String = "1.0.0"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // In production: inject SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleNotifications() {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(notificationsEnabled = !it.preferences.notificationsEnabled))
        }
    }

    fun toggleBackup() {
        _uiState.update {
            it.copy(preferences = it.preferences.copy(backupEnabled = !it.preferences.backupEnabled))
        }
    }
}
