package com.example.csci215_final.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.ui.components.toDisplayDate
import com.example.csci215_final.ui.components.toDisplayDateTime
import com.example.csci215_final.viewmodel.HomeViewModel
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToNewTask: () -> Unit,
    onNavigateToNewReminder: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToEditReminder: (Long) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel { ServiceLocator.homeViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val tasks by viewModel.todayTasks.collectAsState()
    val reminders by viewModel.activeReminders.collectAsState()

    var taskDetailDialog by remember { mutableStateOf<TaskWithRelations?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Check it Off",
                        fontFamily = FontFamily(Font(Res.font.Brownist))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadTodayQuote() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh quote")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewTask) {
                Icon(Icons.Default.Add, contentDescription = "New Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(50.dp))
                Text(
                    text = "Welcome Back!",
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 76.sp,
                        fontFamily = FontFamily(Font(Res.font.Brownist)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    )
                )
            }
            // ── Quote of the Day ──────────────────────────────────────────
            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Quote of the Day",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(6.dp))
                        when {
                            uiState.isQuoteLoading -> {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(Modifier.size(24.dp))
                                }
                            }
                            uiState.quoteError != null -> {
                                Text(
                                    "Could not load quote.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            uiState.quote != null -> {
                                Text(
                                    "“${uiState.quote!!.text}”",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "— ${uiState.quote!!.author}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            // ── Today's Tasks ─────────────────────────────────────────────
            item {
                SectionHeader("Today's Tasks")
            }

            if (tasks.isEmpty()) {
                item {
                    EmptyState("No tasks yet. Tap + to add one.")
                }
            } else {
                items(tasks, key = { it.task.id }) { taskWithRelations ->
                    TaskCard(
                        taskWithRelations = taskWithRelations,
                        onViewHelpersDistractions = { taskDetailDialog = taskWithRelations },
                        onEditTask = { onNavigateToEditTask(taskWithRelations.task.id) }
                    )
                }
            }

            // ── Reminders ─────────────────────────────────────────────────
            item { SectionHeader("Reminders") }

            if (reminders.isEmpty()) {
                item {
                    EmptyState("No active reminders.")
                }
            } else {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onClick = { onNavigateToEditReminder(reminder.id) }
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = onNavigateToNewReminder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Add Reminder")
                }
                Spacer(Modifier.height(80.dp)) // FAB clearance
            }
        }
    }

    // ── Helpers & Distractions detail dialog ──────────────────────────────
    taskDetailDialog?.let { taskRel ->
        AlertDialog(
            onDismissRequest = { taskDetailDialog = null },
            title = { Text(taskRel.task.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (taskRel.helpers.isNotEmpty()) {
                        Text("Helpers", fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge)
                        taskRel.helpers.forEach { h ->
                            Text("• ${h.name}", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Text("No helpers assigned.", style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    if (taskRel.distractions.isNotEmpty()) {
                        Text("Distractions to avoid", fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge)
                        taskRel.distractions.forEach { d ->
                            Text("• ${d.name}", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Text("No distractions assigned.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { taskDetailDialog = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun TaskCard(
    taskWithRelations: TaskWithRelations,
    onViewHelpersDistractions: () -> Unit,
    onEditTask: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    taskWithRelations.task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (taskWithRelations.task.isCompleted) {
                    Text(
                        "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (taskWithRelations.task.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    taskWithRelations.task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Due: ${taskWithRelations.task.dueDate.toDisplayDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onViewHelpersDistractions, modifier = Modifier.weight(1f)) {
                    Text("View Helpers & Distractions", maxLines = 1)
                }
                Button(onClick = onEditTask, modifier = Modifier.weight(1f)) {
                    Text("Edit Task")
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(reminder.title, fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge)
                Text(
                    "${reminder.frequency.name.lowercase().replaceFirstChar { it.uppercase() }} · ${reminder.scheduledTime.toDisplayDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column {
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        HorizontalDivider(Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
