package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.repository.DistractionRepository
import com.example.csci215_final.repository.HelperRepository
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class NewTaskUiState(
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val selectedHelperIds: Set<Long> = emptySet(),
    val selectedDistractionIds: Set<Long> = emptySet(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class NewTaskViewModel(
    private val taskRepository: TaskRepository,
    private val helperRepository: HelperRepository,
    private val distractionRepository: DistractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewTaskUiState())
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    val allHelpers: StateFlow<List<Helper>> =
        helperRepository.getAllHelpers().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allDistractions: StateFlow<List<Distraction>> =
        distractionRepository.getAllDistractions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onDueDateChange(millis: Long) {
        _uiState.value = _uiState.value.copy(dueDateMillis = millis)
    }

    fun toggleHelper(helperId: Long) {
        val current = _uiState.value.selectedHelperIds
        _uiState.value = _uiState.value.copy(
            selectedHelperIds = if (helperId in current) current - helperId else current + helperId
        )
    }

    fun toggleDistraction(distractionId: Long) {
        val current = _uiState.value.selectedDistractionIds
        _uiState.value = _uiState.value.copy(
            selectedDistractionIds = if (distractionId in current) current - distractionId else current + distractionId
        )
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
                createdAt = now
            )
            val taskId = taskRepository.insertTask(task)
            taskRepository.replaceHelpersForTask(taskId, state.selectedHelperIds.toList())
            taskRepository.replaceDistractionsForTask(taskId, state.selectedDistractionIds.toList())
            _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
        }
    }
}
