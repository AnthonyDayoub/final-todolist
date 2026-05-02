package com.example.csci215_final.ui.navigation

import kotlinx.serialization.Serializable

@Serializable data object HomeRoute
@Serializable data object NewTaskRoute
@Serializable data object NewReminderRoute
@Serializable data class  ExistingTaskRoute(val taskId: Long)
@Serializable data class  ExistingReminderRoute(val reminderId: Long)
@Serializable data object HelperDistractionRoute
@Serializable data object ArchiveRoute
