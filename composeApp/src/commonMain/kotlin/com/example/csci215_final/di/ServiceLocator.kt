package com.example.csci215_final.di

import com.example.csci215_final.data.local.database.AppDatabase
import com.example.csci215_final.data.remote.QuoteApiService
import com.example.csci215_final.repository.DistractionRepository
import com.example.csci215_final.repository.HelperRepository
import com.example.csci215_final.repository.QuoteRepository
import com.example.csci215_final.repository.ReminderRepository
import com.example.csci215_final.repository.TaskRepository
import com.example.csci215_final.repository.impl.DistractionRepositoryImpl
import com.example.csci215_final.repository.impl.HelperRepositoryImpl
import com.example.csci215_final.repository.impl.QuoteRepositoryImpl
import com.example.csci215_final.repository.impl.ReminderRepositoryImpl
import com.example.csci215_final.repository.impl.TaskRepositoryImpl
import com.example.csci215_final.viewmodel.ExistingReminderViewModel
import com.example.csci215_final.viewmodel.ExistingTaskViewModel
import com.example.csci215_final.viewmodel.HelperDistractionViewModel
import com.example.csci215_final.viewmodel.HomeViewModel
import com.example.csci215_final.viewmodel.NewReminderViewModel
import com.example.csci215_final.viewmodel.NewTaskViewModel

// Lightweight manual DI — call ServiceLocator.init(db) once in Application/App entry point
object ServiceLocator {

    private lateinit var db: AppDatabase

    val quoteApiService: QuoteApiService by lazy { QuoteApiService() }

    val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(db.taskDao()) }
    val helperRepository: HelperRepository by lazy { HelperRepositoryImpl(db.helperDao()) }
    val distractionRepository: DistractionRepository by lazy { DistractionRepositoryImpl(db.distractionDao()) }
    val reminderRepository: ReminderRepository by lazy { ReminderRepositoryImpl(db.reminderDao()) }
    val quoteRepository: QuoteRepository by lazy { QuoteRepositoryImpl(quoteApiService) }

    fun init(database: AppDatabase) {
        db = database
    }

    fun homeViewModel() = HomeViewModel(taskRepository, reminderRepository, quoteRepository)

    fun newTaskViewModel() = NewTaskViewModel(taskRepository, helperRepository, distractionRepository)

    fun newReminderViewModel() = NewReminderViewModel(reminderRepository)

    fun existingTaskViewModel(taskId: Long) =
        ExistingTaskViewModel(taskId, taskRepository, helperRepository, distractionRepository)

    fun existingReminderViewModel(reminderId: Long) =
        ExistingReminderViewModel(reminderId, reminderRepository)

    fun helperDistractionViewModel() =
        HelperDistractionViewModel(helperRepository, distractionRepository)
}
