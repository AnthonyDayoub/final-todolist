package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Quote
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.QuoteRepository
import com.example.csci215_final.repository.ReminderRepository
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val quote: Quote? = null,
    val isQuoteLoading: Boolean = false,
    val quoteError: String? = null
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val quoteRepository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isQuoteLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Today's tasks (with helpers/distractions/reminders) — observed by the UI
    val todayTasks: StateFlow<List<TaskWithRelations>> =
        taskRepository.getAllTasksWithRelations().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Active reminders shown on the home screen
    val activeReminders: StateFlow<List<Reminder>> =
        reminderRepository.getActiveReminders().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadTodayQuote()
    }

    fun loadTodayQuote() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isQuoteLoading = true, quoteError = null)
            quoteRepository.getTodayQuote()
                .onSuccess { quote ->
                    _uiState.value = _uiState.value.copy(quote = quote, isQuoteLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isQuoteLoading = false,
                        quoteError = error.message ?: "Failed to load quote"
                    )
                }
        }
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            // 1. Fetch the existing task from the repository
            val task = taskRepository.getTaskById(taskId) ?: return@launch

            // 2. Update only the completion status
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }
}
