package com.example.csci215_final.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.domain.model.Reminder
import com.example.csci215_final.domain.model.TaskWithRelations
import com.example.csci215_final.ui.components.toShortTime
import com.example.csci215_final.viewmodel.HomeViewModel
import csci215final.composeapp.generated.resources.*
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

// --- THEME CONSTANTS ---
val CustomRed = Color(0xFF870000)
val CustomLightOrange = Color(0xFFF2C9AC)
val CardBackground = Color.White.copy(alpha = 0.9f)

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
    val reminders by viewModel.remindersForSelectedDate.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showNewTaskSheet by remember { mutableStateOf(false) }
    var showNewReminderSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Layer
        Image(
            painter = painterResource(Res.drawable.looseLeafPaperBKGD),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Check it Off",
                            fontFamily = FontFamily(Font(Res.font.Brownist)),
                            fontSize = 28.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadTodayQuote() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = CustomRed,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToNewTask,
                    containerColor = CustomRed,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
            ) {
                // Header & Calendar Section
                item {
                    Spacer(Modifier.height(16.dp))
                    GreetingHeader(uiState.greeting, tasks.size, reminders.size)
                    WeekCalendar(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.selectDate(it) }
                    )
                }

                // Reminders Section
                item { SectionHeader("Reminders") }
                if (reminders.isEmpty()) {
                    item { EmptyState("No reminders for today.") }
                } else {
                    items(reminders, key = { "rem_${it.id}" }) { reminder ->
                        ReminderCard(reminder) { onNavigateToEditReminder(reminder.id) }
                    }
                }

                // Tasks Section
                item { SectionHeader("Today's Tasks") }
                if (tasks.isEmpty()) {
                    item { EmptyState("Your list is clear!") }
                } else {
                    items(tasks, key = { "task_${it.task.id}" }) { taskWithRelations ->
                        TaskCard(
                            taskWithRelations = taskWithRelations,
                            onEditTask = { onNavigateToEditTask(taskWithRelations.task.id) },
                            onToggleCompletion = { isChecked ->
                                viewModel.updateTaskCompletion(taskWithRelations.task.id, isChecked)
                            }
                        )
                    }
                }

                // Quote Section
                item { QuoteCard(uiState) }
            }
        }
    }

    // Bottom Sheets
    if (showNewTaskSheet) {
        ModalBottomSheet(onDismissRequest = { showNewTaskSheet = false }) {
            Text("Add New Task UI Goes Here", modifier = Modifier.padding(32.dp))
        }
    }
}

/**
 * Cleaned up Header with better typography
 */
@Composable
private fun GreetingHeader(greeting: String, taskCount: Int, reminderCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = greeting,
            style = TextStyle(
                fontSize = 42.sp,
                fontFamily = FontFamily(Font(Res.font.Brownist)),
                color = CustomRed
            )
        )
        Text(
            text = "$taskCount tasks · $reminderCount reminders today",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                color = Color.DarkGray
            )
        )
    }
}

/**
 * Task Card with better hit-targets and cleaner visuals
 */
@Composable
private fun TaskCard(
    taskWithRelations: TaskWithRelations,
    onEditTask: () -> Unit,
    onToggleCompletion: (Boolean) -> Unit
) {
    val isDone = taskWithRelations.task.isCompleted

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onEditTask() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    if (isDone) Res.drawable.filled_check else Res.drawable.empty_check
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onToggleCompletion(!isDone) }
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = taskWithRelations.task.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.Papernotes)),
                        textDecoration = if (isDone) TextDecoration.LineThrough else null,
                        color = if (isDone) Color.Gray else Color.Black
                    )
                )
                Text(
                    text = taskWithRelations.task.dueDate.toShortTime(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = CustomRed,
                        fontFamily = FontFamily(Font(Res.font.Papernotes))
                    )
                )
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CustomLightOrange.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                reminder.scheduledTime.toShortTime(),
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                reminder.title,
                style = TextStyle(fontSize = 18.sp, fontFamily = FontFamily(Font(Res.font.Papernotes)))
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            text = title.uppercase(),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        )
        HorizontalDivider(Modifier.padding(top = 4.dp), thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
private fun EmptyState(message: String) {
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        textAlign = TextAlign.Center,
        style = TextStyle(color = Color.Gray, fontStyle = FontStyle.Italic)
    )
}

@Composable
private fun QuoteCard(uiState: com.example.csci215_final.viewmodel.HomeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CustomLightOrange.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            if (uiState.isQuoteLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.quote != null) {
                Text("“${uiState.quote.text}”", fontStyle = FontStyle.Italic, fontSize = 16.sp)
                Text("- ${uiState.quote.author}", modifier = Modifier.align(Alignment.End), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun WeekCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysToSubtract = if (selectedDate.dayOfWeek.isoDayNumber == 7) 0 else selectedDate.dayOfWeek.isoDayNumber
    val weekStart = selectedDate.minus(DatePeriod(days = daysToSubtract))
    val daysOfWeekLabels = listOf("SU", "M", "T", "W", "R", "F", "SA")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CustomLightOrange.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation Arrow: Back 1 Week
                IconButton(onClick = { onDateSelected(selectedDate.minus(DatePeriod(days = 7))) }) {
                    Text("<", fontSize = 24.sp, fontFamily = FontFamily(Font(Res.font.Brownist)))
                }

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
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Navigation Arrow: Forward 1 Week
                IconButton(onClick = { onDateSelected(selectedDate.plus(DatePeriod(days = 7))) }) {
                    Text(">", fontSize = 24.sp, fontFamily = FontFamily(Font(Res.font.Brownist)))
                }
            }

            Spacer(Modifier.height(8.dp))

            // Your original active day display text
            Text(
                text = "${selectedDate.dayOfWeek.name}, ${selectedDate.month.name} ${selectedDate.dayOfMonth}".uppercase(),
                fontFamily = FontFamily(Font(Res.font.Brownist)),
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                color = CustomRed
            )
        }
    }
}