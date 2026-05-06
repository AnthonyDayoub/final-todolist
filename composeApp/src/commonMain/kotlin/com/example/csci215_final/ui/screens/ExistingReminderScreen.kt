package com.example.csci215_final.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.domain.model.ReminderFrequency
import com.example.csci215_final.ui.components.PurpleButton
import com.example.csci215_final.ui.components.toDisplayDate
import com.example.csci215_final.ui.components.toShortTime
import com.example.csci215_final.viewmodel.ExistingReminderViewModel
import csci215final.composeapp.generated.resources.Bobby
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Papernotes
import csci215final.composeapp.generated.resources.Res
import csci215final.composeapp.generated.resources.looseLeafPaperBKGD
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingReminderScreen(reminderId: Long, onNavigateBack: () -> Unit) {
    val viewModel: ExistingReminderViewModel = viewModel(key = "existing_reminder_$reminderId") {
        ServiceLocator.existingReminderViewModel(reminderId)
    }
    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.scheduledTimeMillis.takeIf { it > 0 }
    )

    val initialDt = remember(uiState.isLoading) {
        if (uiState.scheduledTimeMillis > 0)
            Instant.fromEpochMilliseconds(uiState.scheduledTimeMillis).toLocalDateTime(TimeZone.currentSystemDefault())
        else null
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialDt?.hour ?: 9,
        initialMinute = initialDt?.minute ?: 0,
        is24Hour = false
    )

    val bobbyFont = FontFamily(Font(Res.font.Bobby))

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onNavigateBack() }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.looseLeafPaperBKGD),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Edit Reminder",
                            fontFamily = FontFamily(Font(Res.font.Brownist))
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete, contentDescription = "Delete reminder",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = {
                        Text(
                            text = "Title of Reminder",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Right,
                            )
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.error != null && uiState.title.isBlank()
                )

                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = viewModel::onMessageChange,
                    label = {
                        Text(
                            text = "Description of Reminder",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Right,
                            )
                        )
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // Active toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Active",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = uiState.isActive,
                        onCheckedChange = viewModel::onActiveToggle
                    )
                }

                HorizontalDivider()

                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Date: ${
                            Instant.fromEpochMilliseconds(uiState.scheduledTimeMillis)
                                .toDisplayDate()
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text("Pick Date",
                        fontFamily = bobbyFont)
                    }
                }

                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Time: ${
                            Instant.fromEpochMilliseconds(uiState.scheduledTimeMillis).toShortTime()
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = { showTimePicker = true }) {
                        Text("Pick Time",
                            fontFamily = bobbyFont
                        )
                    }
                }

                HorizontalDivider()

                // Frequency
                Text(
                    "Repeat",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReminderFrequency.entries.forEach { freq ->
                        FilterChip(
                            selected = uiState.frequency == freq,
                            onClick = { viewModel.onFrequencyChange(freq) },
                            label = {
                                Text(
                                    freq.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontFamily = bobbyFont
                                )
                            }
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(
                        uiState.error!!, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                PurpleButton(
                    text = "Save Changes",
                    onClick = viewModel::saveChanges,
                    isLoading = uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { viewModel.onScheduledTimeChange(it) }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onScheduleTimeChange(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                },
                text = { TimePicker(state = timePickerState) }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Reminder?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteReminder()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
