package com.memora.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    data object Loading : SplashDestination()
    data object Auth : SplashDestination()
    data object Home : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    // In production: inject AuthRepository + SettingsDataStore to check auth state
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            // Simulate initialization: check auth, preload, etc.
            delay(1600L)
            // Skip onboarding — go directly to Auth or Home
            // In production: check if user is authenticated
            _destination.value = SplashDestination.Auth
        }
    }
}
