package com.memora.feature.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class RemindersUiState(
    val upcoming: List<Reminder> = emptyList(),
    val past: List<Reminder> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RemindersViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init { loadReminders() }

    fun completeReminder(id: String) {
        _uiState.update { state ->
            val item = state.upcoming.find { it.id == id } ?: return@update state
            state.copy(
                upcoming = state.upcoming.filter { it.id != id },
                past = listOf(item.copy(isCompleted = true)) + state.past
            )
        }
    }

    private fun loadReminders() {
        viewModelScope.launch {
            delay(400)
            val now = Instant.now()
            _uiState.value = RemindersUiState(
                isLoading = false,
                upcoming = listOf(
                    Reminder("r1", "1", "Assignment 3 Due", "ML course assignment", Instant.ofEpochMilli(now.toEpochMilli() + 86400000 * 9), false, "ai_suggested", now, now),
                    Reminder("r2", "4", "Dr. Sharma Follow-up", "Blood pressure check", Instant.ofEpochMilli(now.toEpochMilli() + 86400000 * 14), false, "ai_suggested", now, now),
                    Reminder("r3", "7", "Return Library Books", "Before Aug 10 deadline", Instant.ofEpochMilli(now.toEpochMilli() + 86400000 * 4), false, "ai_suggested", now, now),
                ),
                past = listOf(
                    Reminder("r4", "3", "Hackathon Registration", "TechFest 2026", Instant.ofEpochMilli(now.toEpochMilli() - 86400000), true, "user_created", now, now),
                )
            )
        }
    }
}
