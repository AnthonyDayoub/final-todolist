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
import androidx.compose.foundation.layout.width
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.Clock
import com.example.csci215_final.ui.components.toShortTime
import csci215final.composeapp.generated.resources.Papernotes
import androidx.compose.foundation.Image
import csci215final.composeapp.generated.resources.empty_check
import csci215final.composeapp.generated.resources.filled_check
import org.jetbrains.compose.resources.painterResource

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

    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var selectedDate by remember { mutableStateOf(today) }

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
            item(key = "welcome") {
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
                WeekCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }

            // ── Reminders ─────────────────────────────────────────────────
            item(key = "reminders_header") {
                SectionHeader("Reminders")

            }

            if (reminders.isEmpty()) {
                item(key = "reminders_empty") {
                    EmptyState("No active reminders.")
                }
            } else {
                items(reminders, key = { "reminder_${it.id}" }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onClick = { onNavigateToEditReminder(reminder.id) }
                    )
                }
            }

            item(key = "add_reminder_button") {
                OutlinedButton(
                    onClick = onNavigateToNewReminder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Add Reminder")
                }
                Spacer(Modifier.height(40.dp)) // FAB clearance
            }

            // ── Today's Tasks ─────────────────────────────────────────────
            item(key = "tasks_header") {
                SectionHeader("Today's Tasks")
            }

            if (tasks.isEmpty()) {
                item(key = "tasks_empty") {
                    EmptyState("No tasks yet. Tap + to add one.")
                }
            } else {
                items(tasks, key = { "task_${it.task.id}" }) { taskWithRelations ->
                    TaskCard(
                        taskWithRelations = taskWithRelations,
                        onEditTask = { onNavigateToEditTask(taskWithRelations.task.id) },
                        onToggleCompletion = { isChecked ->
                            // Make sure your ViewModel has this function!
                            viewModel.updateTaskCompletion(taskWithRelations.task.id, isChecked)
                        }
                    )
                }
            }

            // ── Quote of the Day ──────────────────────────────────────────
            item (key = "quote"){
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
    onEditTask: () -> Unit,
    // Add an onToggle callback to update the database via ViewModel
    onToggleCompletion: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditTask() }, // Making the whole card clickable for editing
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            // --- First Row: Checkbox and Title ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val isDone = taskWithRelations.task.isCompleted

                // Interactable Checkbox (Flipping between PNGs)
                Image(
                    painter = painterResource(
                        if (isDone) Res.drawable.filled_check else Res.drawable.empty_check
                    ),
                    contentDescription = if (isDone) "Completed" else "Incomplete",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onToggleCompletion(!isDone) }
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = taskWithRelations.task.title,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(Res.font.Papernotes)),
                        textDecoration = if (isDone) TextDecoration.LineThrough else null
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(Modifier.height(12.dp))

            // --- Second Row: Distractions (Left) and Helpers (Right) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Distractions
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "DISTRACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    if (taskWithRelations.distractions.isEmpty()) {
                        Text("None", style = MaterialTheme.typography.bodySmall)
                    } else {
                        taskWithRelations.distractions.forEach { d ->
                            Text("• ${d.name}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                // Right Column: Helpers
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        "HELPERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    if (taskWithRelations.helpers.isEmpty()) {
                        Text("None", style = MaterialTheme.typography.bodySmall)
                    } else {
                        taskWithRelations.helpers.forEach { h ->
                            Text("• ${h.name}", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End)
                        }
                    }
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
            containerColor = Color(0xFFE0E0E0)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reminder.scheduledTime.toShortTime(),
                    modifier = Modifier.width(80.dp),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(Res.font.Papernotes)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Left,
                    )
                )
                Text(
                    text = reminder.title,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(Res.font.Papernotes)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Right, // Aligns text within its area to the right
                    )
                )
            }
        }
    }
}
@Composable
private fun SectionHeader(title: String) {
    Column {
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                fontWeight = FontWeight(400),
                color = Color(0xFF000000),
                textAlign = TextAlign.Right,
            )
        )
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

@Composable
fun WeekCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // Helper to find the most recent Sunday
    // In kotlinx.datetime, Monday is 1, Sunday is 7.
    val daysToSubtract = if (selectedDate.dayOfWeek.isoDayNumber == 7) 0 else selectedDate.dayOfWeek.isoDayNumber
    val weekStart = selectedDate.minus(DatePeriod(days = daysToSubtract))

    val daysOfWeekLabels = listOf("SU", "M", "T", "W", "R", "F", "SA")

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Week
            IconButton(onClick = { onDateSelected(selectedDate.minus(DatePeriod(days = 7))) }) {
                Text("<", fontSize = 24.sp, fontFamily = FontFamily(Font(Res.font.Brownist)))
            }

            // Days of the week
            daysOfWeekLabels.forEachIndexed { index, dayLabel ->
                val dateForDay = weekStart.plus(DatePeriod(days = index))
                val isSelected = dateForDay == selectedDate

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onDateSelected(dateForDay) }
                        .padding(4.dp)
                ) {
                    Text(
                        text = dayLabel,
                        fontFamily = FontFamily(Font(Res.font.Brownist)),
                        fontSize = 28.sp,
                        color = if (isSelected) Color.Black else Color.Gray,
                        textDecoration = if (isSelected) TextDecoration.Underline else null
                    )
                }
            }

            // Next Week
            IconButton(onClick = { onDateSelected(selectedDate.plus(DatePeriod(days = 7))) }) {
                Text(">", fontSize = 24.sp, fontFamily = FontFamily(Font(Res.font.Brownist)))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Active Day Display (e.g., SUNDAY, APRIL 15)
        Text(
            text = "${selectedDate.dayOfWeek.name}, ${selectedDate.month.name} ${selectedDate.dayOfMonth}".uppercase(),
            fontFamily = FontFamily(Font(Res.font.Brownist)),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}