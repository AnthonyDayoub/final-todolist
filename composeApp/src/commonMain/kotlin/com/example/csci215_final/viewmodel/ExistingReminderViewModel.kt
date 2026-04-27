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
    val error: String? = null
)

class ExistingReminderViewModel(
    private val reminderId: Long,
    private val reminderRepository: ReminderRepository
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
                isLoading = false
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
        _uiState.value = _uiState.value.copy(scheduledTimeMillis = millis)
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
                isActive = state.isActive
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
