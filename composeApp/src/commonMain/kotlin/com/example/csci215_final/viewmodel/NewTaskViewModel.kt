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

data class NewTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class NewTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewTaskUiState())
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) { _uiState.value = _uiState.value.copy(title = title) }
    fun onDescriptionChange(description: String) { _uiState.value = _uiState.value.copy(description = description) }
    fun onDueDateChange(millis: Long) { _uiState.value = _uiState.value.copy(dueDateMillis = millis) }

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
                createdAt = now
            )
            taskRepository.insertTask(task)
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}