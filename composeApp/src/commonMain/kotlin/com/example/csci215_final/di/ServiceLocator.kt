package com.example.csci215_final.di

import com.example.csci215_final.data.local.database.AppDatabase
import com.example.csci215_final.data.remote.QuoteApiService
import com.example.csci215_final.preferences.AppPreferences
import com.example.csci215_final.repository.QuoteRepository
import com.example.csci215_final.repository.ReminderRepository
import com.example.csci215_final.repository.TaskRepository
import com.example.csci215_final.repository.impl.QuoteRepositoryImpl
import com.example.csci215_final.repository.impl.ReminderRepositoryImpl
import com.example.csci215_final.repository.impl.TaskRepositoryImpl
import com.example.csci215_final.viewmodel.ArchiveViewModel
import com.example.csci215_final.viewmodel.ExistingReminderViewModel
import com.example.csci215_final.viewmodel.ExistingTaskViewModel
import com.example.csci215_final.viewmodel.HomeViewModel
import com.example.csci215_final.viewmodel.NewReminderViewModel
import com.example.csci215_final.viewmodel.NewTaskViewModel

object ServiceLocator {
    private lateinit var db: AppDatabase
    private lateinit var prefs: AppPreferences

    val quoteApiService: QuoteApiService by lazy { QuoteApiService() }

    val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(db.taskDao()) }
    val reminderRepository: ReminderRepository by lazy { ReminderRepositoryImpl(db.reminderDao()) }
    val quoteRepository: QuoteRepository by lazy { QuoteRepositoryImpl(quoteApiService) }

    fun init(database: AppDatabase, preferences: AppPreferences) {
        db = database
        prefs = preferences
    }

    fun homeViewModel(): HomeViewModel {
        val firstLaunch = prefs.isFirstLaunch()
        if (firstLaunch) prefs.markLaunched()
        return HomeViewModel(taskRepository, reminderRepository, quoteRepository, firstLaunch)
    }

    fun newTaskViewModel() = NewTaskViewModel(taskRepository)

    fun newReminderViewModel() = NewReminderViewModel(reminderRepository)

    fun existingTaskViewModel(taskId: Long) = ExistingTaskViewModel(taskId, taskRepository)

    fun existingReminderViewModel(reminderId: Long) = ExistingReminderViewModel(reminderId, reminderRepository)

    fun archiveViewModel() = ArchiveViewModel(taskRepository, reminderRepository)
}
