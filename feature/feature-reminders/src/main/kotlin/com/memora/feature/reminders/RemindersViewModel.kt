package com.memora.feature.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memora.core.common.reminder.LocalReminderScheduler
import com.memora.core.database.dao.ReminderDao
import com.memora.core.database.entity.ReminderEntity
import com.memora.core.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class RemindersUiState(
    val upcoming: List<Reminder> = emptyList(),
    val past: List<Reminder> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val reminderDao: ReminderDao,
    private val localReminderScheduler: LocalReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init { loadReminders() }

    fun completeReminder(id: String) {
        viewModelScope.launch {
            reminderDao.completeReminder(id)
            localReminderScheduler.cancel(id)
        }
    }

    /** Persists and schedules a reminder locally; it does not require Firebase or a network. */
    fun createReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderDao.insertReminder(reminder.toEntity())
            if (!reminder.isCompleted) {
                localReminderScheduler.schedule(
                    id = reminder.id,
                    title = reminder.title,
                    description = reminder.description,
                    triggerAtMillis = reminder.dateTime.toEpochMilli()
                )
            }
        }
    }

    private fun loadReminders() {
        viewModelScope.launch {
            combine(
                reminderDao.getUpcomingReminders(),
                reminderDao.getPastReminders()
            ) { upcoming, past ->
                RemindersUiState(
                    upcoming = upcoming.map { it.toDomain() },
                    past = past.map { it.toDomain() },
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}

private fun Reminder.toEntity() = ReminderEntity(
    id = id,
    memoryId = memoryId,
    title = title,
    description = description,
    dateTime = dateTime.toEpochMilli(),
    isCompleted = isCompleted,
    source = source,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

private fun ReminderEntity.toDomain() = Reminder(
    id = id,
    memoryId = memoryId,
    title = title,
    description = description,
    dateTime = Instant.ofEpochMilli(dateTime),
    isCompleted = isCompleted,
    source = source,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)
