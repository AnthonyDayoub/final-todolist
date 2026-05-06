package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.TaskRepository
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

data class ExistingTaskUiState(
    val taskWithRelations: TaskWithRelations? = null,
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = 0L,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
)

class ExistingTaskViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExistingTaskUiState())
    val uiState: StateFlow<ExistingTaskUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val taskWithRelations = taskRepository.getTaskWithRelations(taskId)
            if (taskWithRelations == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Task not found")
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                taskWithRelations = taskWithRelations,
                title = taskWithRelations.task.title,
                description = taskWithRelations.task.description,
                dueDateMillis = taskWithRelations.task.dueDate.toEpochMilliseconds(),
                isLoading = false,
            )
        }
    }

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

    fun saveChanges() {
        val state = _uiState.value
        val original = state.taskWithRelations ?: return
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            val updated = original.task.copy(
                title = state.title.trim(),
                description = state.description.trim(),
                dueDate = kotlinx.datetime.Instant.fromEpochMilliseconds(state.dueDateMillis),
            )
            taskRepository.updateTask(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId) ?: return@launch
            taskRepository.deleteTask(task)
        }
    }
}
