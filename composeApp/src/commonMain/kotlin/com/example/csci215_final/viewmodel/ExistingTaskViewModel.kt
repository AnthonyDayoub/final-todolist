package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.DistractionRepository
import com.example.csci215_final.repository.HelperRepository
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExistingTaskUiState(
    val taskWithRelations: TaskWithRelations? = null,
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long = 0L,
    val selectedHelperIds: Set<Long> = emptySet(),
    val selectedDistractionIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class ExistingTaskViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
    private val helperRepository: HelperRepository,
    private val distractionRepository: DistractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExistingTaskUiState())
    val uiState: StateFlow<ExistingTaskUiState> = _uiState.asStateFlow()

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
                selectedHelperIds = taskWithRelations.helpers.map { it.id }.toSet(),
                selectedDistractionIds = taskWithRelations.distractions.map { it.id }.toSet(),
                isLoading = false
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
                dueDate = kotlinx.datetime.Instant.fromEpochMilliseconds(state.dueDateMillis)
            )
            taskRepository.updateTask(updated)
            taskRepository.replaceHelpersForTask(taskId, state.selectedHelperIds.toList())
            taskRepository.replaceDistractionsForTask(taskId, state.selectedDistractionIds.toList())
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
