package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.repository.TaskRepository
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

data class NewTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

class NewTaskViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NewTaskUiState())
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onDueDateChange(millis: Long) {
        val tz = TimeZone.currentSystemDefault()
        val currentTime = Instant.fromEpochMilliseconds(_uiState.value.dueDateMillis).toLocalDateTime(tz).time
        // DatePicker returns UTC midnight — read the date in UTC to avoid a timezone day-shift
        val newDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
        val combined = LocalDateTime(newDate, currentTime).toInstant(tz).toEpochMilliseconds()
        _uiState.value = _uiState.value.copy(dueDateMillis = combined)
    }

    fun onScheduleTimeChange(hour: Int, minute: Int) {
        val tz = TimeZone.currentSystemDefault()
        val existing = Instant.fromEpochMilliseconds(_uiState.value.dueDateMillis).toLocalDateTime(tz)
        val updated = LocalDateTime(existing.date, LocalTime(hour, minute))
        _uiState.value = _uiState.value.copy(dueDateMillis = updated.toInstant(tz).toEpochMilliseconds())
    }

    fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val now = Clock.System.now()
            val task = Task(
                title = state.title.trim(),
                description = state.description.trim(),
                dueDate = kotlinx.datetime.Instant.fromEpochMilliseconds(state.dueDateMillis),
                createdAt = now,
            )
            taskRepository.insertTask(task)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}
