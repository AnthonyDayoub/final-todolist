package com.example.csci215_final.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.repository.ReminderRepository
import com.example.csci215_final.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArchiveViewModel(
    taskRepository: TaskRepository,
    reminderRepository: ReminderRepository,
) : ViewModel() {
    val completedTasks: StateFlow<List<Task>> = taskRepository
        .getCompletedTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val dismissedReminders: StateFlow<List<Reminder>> = reminderRepository
        .getAllReminders()
        .map { list -> list.filter { !it.isActive } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
