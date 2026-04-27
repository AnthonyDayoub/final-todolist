package com.example.csci215_final.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object NewTask : Screen("new_task")
    data object NewReminder : Screen("new_reminder")
    data object ExistingTask : Screen("existing_task/{taskId}") {
        fun createRoute(taskId: Long) = "existing_task/$taskId"
    }
    data object ExistingReminder : Screen("existing_reminder/{reminderId}") {
        fun createRoute(reminderId: Long) = "existing_reminder/$reminderId"
    }
    data object HelperDistraction : Screen("helpers_distractions")
}
