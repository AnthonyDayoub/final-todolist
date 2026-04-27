package com.example.csci215_final.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.csci215_final.di.ServiceLocator
import com.example.csci215_final.domain.model.Distraction
import com.example.csci215_final.domain.model.Helper
import com.example.csci215_final.viewmodel.HelperDistractionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperDistractionScreen(onNavigateBack: () -> Unit) {
    val viewModel: HelperDistractionViewModel = viewModel { ServiceLocator.helperDistractionViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    val allHelpers by viewModel.allHelpers.collectAsState()
    val allDistractions by viewModel.allDistractions.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Helpers", "Distractions")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Helpers & Distractions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> HelpersTab(
                    helpers = allHelpers,
                    nameValue = uiState.newHelperName,
                    descValue = uiState.newHelperDescription,
                    error = uiState.error,
                    onNameChange = viewModel::onNewHelperNameChange,
                    onDescChange = viewModel::onNewHelperDescriptionChange,
                    onAdd = viewModel::addHelper,
                    onDelete = viewModel::deleteHelper
                )
                1 -> DistractionsTab(
                    distractions = allDistractions,
                    nameValue = uiState.newDistractionName,
                    descValue = uiState.newDistractionDescription,
                    error = uiState.error,
                    onNameChange = viewModel::onNewDistractionNameChange,
                    onDescChange = viewModel::onNewDistractionDescriptionChange,
                    onAdd = viewModel::addDistraction,
                    onDelete = viewModel::deleteDistraction
                )
            }
        }
    }
}

@Composable
private fun HelpersTab(
    helpers: List<Helper>,
    nameValue: String,
    descValue: String,
    error: String?,
    onNameChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Helper) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Text("Add Helper", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nameValue,
                onValueChange = onNameChange,
                label = { Text("Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null && nameValue.isBlank()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descValue,
                onValueChange = onDescChange,
                label = { Text("Description (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                Text("Add Helper")
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text("Your Helpers", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        if (helpers.isEmpty()) {
            item {
                Text("No helpers added yet.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            items(helpers, key = { it.id }) { helper ->
                HelperDistractionItem(
                    name = helper.name,
                    description = helper.description,
                    onDelete = { onDelete(helper)  }
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun DistractionsTab(
    distractions: List<Distraction>,
    nameValue: String,
    descValue: String,
    error: String?,
    onNameChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Distraction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Text("Add Distraction", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nameValue,
                onValueChange = onNameChange,
                label = { Text("Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null && nameValue.isBlank()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = descValue,
                onValueChange = onDescChange,
                label = { Text("Description (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                Text("Add Distraction")
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text("Your Distractions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        if (distractions.isEmpty()) {
            item {
                Text("No distractions added yet.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp))
            }
        } else {
            items(distractions, key = { it.id }) { distraction ->
                HelperDistractionItem(
                    name = distraction.name,
                    description = distraction.description,
                    onDelete = { onDelete(distraction) }
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun HelperDistractionItem(name: String, description: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                if (description.isNotBlank()) {
                    Text(description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
