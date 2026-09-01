package com.memora.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.memora.core.common.result.Result
import com.memora.core.firebase.auth.FirebaseAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLogin: Boolean = true,
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

sealed class AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class DisplayNameChanged(val name: String) : AuthEvent()
    data class GoogleSignInSuccess(val idToken: String) : AuthEvent()
    data class GoogleSignInFailure(val errorMessage: String) : AuthEvent()
    data object ToggleMode : AuthEvent()
    data object Submit : AuthEvent()
    data object DismissError : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Observe auth state changes
        viewModelScope.launch {
            firebaseAuthManager.authState.collect { result ->
                if (result is Result.Success && result.data != null) {
                    _uiState.update { it.copy(isAuthenticated = true, isLoading = false) }
                }
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> _uiState.update { it.copy(email = event.email, error = null) }
            is AuthEvent.PasswordChanged -> _uiState.update { it.copy(password = event.password, error = null) }
            is AuthEvent.DisplayNameChanged -> _uiState.update { it.copy(displayName = event.name, error = null) }
            is AuthEvent.GoogleSignInSuccess -> signInWithGoogle(event.idToken)
            is AuthEvent.GoogleSignInFailure -> _uiState.update { it.copy(isLoading = false, error = event.errorMessage) }
            AuthEvent.ToggleMode -> _uiState.update { it.copy(isLogin = !it.isLogin, error = null) }
            AuthEvent.DismissError -> _uiState.update { it.copy(error = null) }
            AuthEvent.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }
        if (!state.isLogin && state.displayName.isBlank()) {
            _uiState.update { it.copy(error = "Please enter your name") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = if (state.isLogin) {
                firebaseAuthManager.signInWithEmail(state.email, state.password)
            } else {
                firebaseAuthManager.signUpWithEmail(state.email, state.password, state.displayName)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception?.localizedMessage ?: "Authentication failed") }
                }
                Result.Loading -> {}
            }
        }
    }

    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            when (val result = firebaseAuthManager.signInWithCredential(credential)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception?.localizedMessage ?: "Google sign-in failed") }
                }
                Result.Loading -> {}
            }
        }
    }
}
