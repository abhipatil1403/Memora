package com.memora.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.database.dao.ReminderDao
import com.memora.core.database.repository.MemoryRepository
import com.memora.core.firebase.auth.FirebaseAuthManager
import com.memora.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val memoryCount: Int = 0,
    val collectionCount: Int = 0,
    val reminderCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val reminderDao: ReminderDao,
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val currentUser = authManager.getCurrentUser()

        viewModelScope.launch {
            combine(
                memoryRepository.getAllMemories(),
                reminderDao.getUpcomingReminders()
            ) { memories, reminders ->
                val distinctCategories = memories.map { it.category }.distinct().count()
                ProfileUiState(
                    user = currentUser,
                    memoryCount = memories.size,
                    collectionCount = distinctCategories.coerceAtLeast(if (memories.isNotEmpty()) 1 else 0),
                    reminderCount = reminders.size,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                authManager.signOut()
            } catch (_: Exception) {}
            onComplete()
        }
    }
}

