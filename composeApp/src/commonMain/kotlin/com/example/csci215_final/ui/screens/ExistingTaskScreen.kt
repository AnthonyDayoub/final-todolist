package com.example.csci215_final.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import com.example.csci215_final.ui.components.toDisplayDate
import com.example.csci215_final.viewmodel.ExistingTaskViewModel
import csci215final.composeapp.generated.resources.Brownist
import csci215final.composeapp.generated.resources.Papernotes
import csci215final.composeapp.generated.resources.Res
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingTaskScreen(taskId: Long, onNavigateBack: () -> Unit) {
    val viewModel: ExistingTaskViewModel = viewModel(key = "existing_task_$taskId") {
        ServiceLocator.existingTaskViewModel(taskId)
    }
    val uiState by viewModel.uiState.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.dueDateMillis.takeIf { it > 0 }
    )

    LaunchedEffect(uiState.isSaved) { if (uiState.isSaved) onNavigateBack() }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Check it Off",
                        fontFamily = FontFamily(Font(Res.font.Brownist))
                    )
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "edit task") {
                Spacer(Modifier.height(50.dp))
                Text(
                    text = "Edit Task",
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

            item(key = "title_field") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = "Task Name",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                                fontWeight = FontWeight(400),
                                color = Color.Black
                            )
                        )
                    },
                    singleLine = true,
                    isError = uiState.error != null && uiState.title.isBlank()
                )
            }


            item(key = "description_field") {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = {
                        Text(
                            text = "Task Description",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(Res.font.Papernotes)),
                                fontWeight = FontWeight(400),
                                color = Color.Black
                            )
                        )
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(key = "date_row") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Due: ${Instant.fromEpochMilliseconds(uiState.dueDateMillis).toDisplayDate()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Text("Change Date")
                    }
                }
            }

            if (uiState.error != null) {
                item(key = "error_message") {
                    Text(
                        uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item(key = "divider") {
                HorizontalDivider()
            }

            item(key = "save_button") {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = viewModel::saveChanges,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isSaving) "Saving…" else "Save Changes")
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }

    // Dialogs stay outside the LazyColumn/Scaffold flow
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDueDateChange(it) }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTask()
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
