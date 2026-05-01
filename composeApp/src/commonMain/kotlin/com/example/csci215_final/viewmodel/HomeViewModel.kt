package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Quote
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.ReminderFrequency
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.repository.QuoteRepository
import com.example.csci215_final.repository.ReminderRepository
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

data class HomeUiState(
    val isFirstLaunch: Boolean = false,
    val quote: Quote? = null,
    val isQuoteLoading: Boolean = false,
    val quoteError: String? = null,
    val greeting: String = ""
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val quoteRepository: QuoteRepository,
    isFirstLaunch: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isFirstLaunch = isFirstLaunch, isQuoteLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val todayTasks: StateFlow<List<TaskWithRelations>> =
        combine(taskRepository.getAllTasksWithRelations(), _selectedDate) { tasks, date ->
            tasks.filter { twr ->
                twr.task.dueDate.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val remindersForSelectedDate: StateFlow<List<Reminder>> =
        combine(reminderRepository.getActiveReminders(), _selectedDate) { reminders, date ->
            reminders.filter { reminder ->
                val reminderDate = reminder.scheduledTime
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                when (reminder.frequency) {
                    ReminderFrequency.ONCE -> reminderDate == date
                    ReminderFrequency.DAILY -> true
                    ReminderFrequency.WEEKLY -> reminderDate.dayOfWeek == date.dayOfWeek
                    ReminderFrequency.MONTHLY -> reminderDate.dayOfMonth == date.dayOfMonth
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadTodayQuote()
        updateGreeting()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
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

    private fun updateGreeting() {
        val hour = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .hour
        val timeGreeting = when (hour) {
            in 5..11  -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else      -> "Good night"
        }
        _uiState.value = _uiState.value.copy(greeting = timeGreeting)
    }

    fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId) ?: return@launch
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }
}
