package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.ReminderFrequency
import com.example.csci215_final.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class NewReminderUiState(
    val title: String = "",
    val message: String = "",
    val scheduledTimeMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val frequency: ReminderFrequency = ReminderFrequency.ONCE,
    val linkedTaskId: Long? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

class NewReminderViewModel(
    private val reminderRepository: ReminderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewReminderUiState())
    val uiState: StateFlow<NewReminderUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun onScheduledTimeChange(millis: Long) {
        val tz = TimeZone.currentSystemDefault()
        val currentTime = Instant.fromEpochMilliseconds(_uiState.value.scheduledTimeMillis).toLocalDateTime(tz).time
        // DatePicker returns UTC midnight — read the date in UTC to avoid a timezone day-shift
        val newDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
        val combined = LocalDateTime(newDate, currentTime).toInstant(tz).toEpochMilliseconds()
        _uiState.value = _uiState.value.copy(scheduledTimeMillis = combined)
    }

    fun onScheduleTimeChange(hour: Int, minute: Int) {
        val tz = TimeZone.currentSystemDefault()
        val existing = Instant.fromEpochMilliseconds(_uiState.value.scheduledTimeMillis).toLocalDateTime(tz)
        val updated = LocalDateTime(existing.date, LocalTime(hour, minute))
        _uiState.value = _uiState.value.copy(scheduledTimeMillis = updated.toInstant(tz).toEpochMilliseconds())
    }

    fun onFrequencyChange(frequency: ReminderFrequency) {
        _uiState.value = _uiState.value.copy(frequency = frequency)
    }

    fun onLinkedTaskChange(taskId: Long?) {
        _uiState.value = _uiState.value.copy(linkedTaskId = taskId)
    }

    fun saveReminder() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val reminder = Reminder(
                title = state.title.trim(),
                message = state.message.trim(),
                frequency = state.frequency,
                scheduledTime = kotlinx.datetime.Instant.fromEpochMilliseconds(state.scheduledTimeMillis),
                taskId = state.linkedTaskId,
                isActive = true,
            )
            reminderRepository.insertReminder(reminder)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}
