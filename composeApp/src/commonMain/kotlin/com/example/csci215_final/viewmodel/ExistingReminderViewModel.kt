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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

data class ExistingReminderUiState(
    val reminder: Reminder? = null,
    val title: String = "",
    val message: String = "",
    val scheduledTimeMillis: Long = 0L,
    val frequency: ReminderFrequency = ReminderFrequency.ONCE,
    val isActive: Boolean = true,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

class ExistingReminderViewModel(
    private val reminderId: Long,
    private val reminderRepository: ReminderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExistingReminderUiState())
    val uiState: StateFlow<ExistingReminderUiState> = _uiState.asStateFlow()

    init {
        loadReminder()
    }

    private fun loadReminder() {
        viewModelScope.launch {
            val reminder = reminderRepository.getReminderById(reminderId)
            if (reminder == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Reminder not found")
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                reminder = reminder,
                title = reminder.title,
                message = reminder.message,
                scheduledTimeMillis = reminder.scheduledTime.toEpochMilliseconds(),
                frequency = reminder.frequency,
                isActive = reminder.isActive,
                isLoading = false,
            )
        }
    }

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

    fun onActiveToggle(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = isActive)
    }

    fun saveChanges() {
        val state = _uiState.value
        val original = state.reminder ?: return
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val updated = original.copy(
                title = state.title.trim(),
                message = state.message.trim(),
                frequency = state.frequency,
                scheduledTime = kotlinx.datetime.Instant.fromEpochMilliseconds(state.scheduledTimeMillis),
                isActive = state.isActive,
            )
            reminderRepository.updateReminder(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun deleteReminder() {
        viewModelScope.launch {
            val reminder = reminderRepository.getReminderById(reminderId) ?: return@launch
            reminderRepository.deleteReminder(reminder)
        }
    }
}
