package com.example.csci215_final.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.Task
import com.example.csci215_final.ui.components.UniversalItemCard
import com.example.csci215_final.ui.components.toDisplayDate
import com.example.csci215_final.ui.components.toShortTime
import com.example.csci215_final.viewmodel.ArchiveViewModel
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Papernotes
import csci215final.composeapp.generated.resources.Res
import csci215final.composeapp.generated.resources.looseLeafPaperBKGD
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToEditReminder: (Long) -> Unit
) {
    val viewModel: ArchiveViewModel = viewModel { ServiceLocator.archiveViewModel() }
    val completedTasks by viewModel.completedTasks.collectAsState()
    val dismissedReminders by viewModel.dismissedReminders.collectAsState()

    val tasksByDate: Map<String, List<Task>> = completedTasks
        .groupBy { task ->
            task.dueDate.toLocalDateTime(TimeZone.currentSystemDefault()).date.let { date ->
                "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}, ${date.year}"
            }
        }

    val remindersByDate: Map<String, List<Reminder>> = dismissedReminders
        .groupBy { reminder ->
            reminder.scheduledTime.toLocalDateTime(TimeZone.currentSystemDefault()).date.let { date ->
                "${date.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${date.dayOfMonth}, ${date.year}"
            }
        }

    val isEmpty = completedTasks.isEmpty() && dismissedReminders.isEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.looseLeafPaperBKGD),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Archive",
                            fontFamily = FontFamily(Font(Res.font.Brownist))
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CustomRed,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                )
            }
        ) { paddingValues ->
            if (isEmpty) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nothing archived yet.",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(Res.font.Papernotes)),
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (completedTasks.isNotEmpty()) {
                        item(key = "tasks_header") {
                            SectionHeader("Completed Tasks")
                        }
                        tasksByDate.forEach { (dateLabel, tasks) ->
                            item(key = "header_task_$dateLabel") {
                                DateSectionHeader(dateLabel)
                            }
                            items(tasks, key = { "archive_task_${it.id}" }) { task ->
                                UniversalItemCard(
                                    title = task.title,
                                    time = task.dueDate.toShortTime(),
                                    description = task.description.ifBlank { "No description" },
                                    isTask = true,
                                    isCompleted = true,
                                    onCheckedChange = {},
                                    onClick = { onNavigateToEditTask(task.id) }
                                )
                            }
                        }
                    }

                    if (dismissedReminders.isNotEmpty()) {
                        item(key = "reminders_header") {
                            SectionHeader("Dismissed Reminders")
                        }
                        remindersByDate.forEach { (dateLabel, reminders) ->
                            item(key = "header_rem_$dateLabel") {
                                DateSectionHeader(dateLabel)
                            }
                            items(reminders, key = { "archive_rem_${it.id}" }) { reminder ->
                                UniversalItemCard(
                                    title = reminder.title,
                                    time = reminder.scheduledTime.toShortTime(),
                                    description = reminder.message.ifBlank { "No description" },
                                    isTask = false,
                                    isCompleted = true,
                                    onCheckedChange = {},
                                    onClick = { onNavigateToEditReminder(reminder.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 4.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Brownist)),
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        )
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

@Composable
private fun DateSectionHeader(label: String) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = TextStyle(
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(Res.font.Brownist)),
                color = CustomRed,
                letterSpacing = 1.sp
            )
        )
        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = CustomLightOrange
        )
    }
}
