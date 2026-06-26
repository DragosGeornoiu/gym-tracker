package com.dragos.geornoiu.gymtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.LoadMode
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntry
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    workoutTitle: String,
    workoutEntries: List<WorkoutEntry>,
    onBackClick: () -> Unit,
    onAddWorkoutEntry: (String, WorkoutEntryType, LoadMode) -> Unit,
    onWorkoutEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(WorkoutEntryType.STRENGTH) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var selectedLoadMode by remember { mutableStateOf(LoadMode.TOTAL) }
    var loadModeMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Workout",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = workoutTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Exercise entries",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (workoutEntries.isEmpty()) {
                Text(
                    text = "No exercise entries yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                workoutEntries.forEach { entry ->
                    Card(
                        onClick = { onWorkoutEntryClick(entry.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = entry.type.displayName(),
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (entry.type == WorkoutEntryType.STRENGTH) {
                                Text(
                                    text = "Load: ${entry.loadMode.displayName()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Exercise name") },
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = !typeMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    label = { Text("Entry type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { typeMenuExpanded = false }
                ) {
                    listOf(
                        WorkoutEntryType.STRENGTH,
                        WorkoutEntryType.CARDIO,
                        WorkoutEntryType.CIRCUIT,
                        WorkoutEntryType.STRETCHING
                    ).forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName()) },
                            onClick = {
                                selectedType = type
                                typeMenuExpanded = false

                                if (type != WorkoutEntryType.STRENGTH) {
                                    selectedLoadMode = LoadMode.TOTAL
                                    loadModeMenuExpanded = false
                                }
                            }
                        )
                    }
                }
            }

            if (selectedType == WorkoutEntryType.STRENGTH) {
                ExposedDropdownMenuBox(
                    expanded = loadModeMenuExpanded,
                    onExpandedChange = { loadModeMenuExpanded = !loadModeMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLoadMode.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Load mode") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = loadModeMenuExpanded
                            )
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = loadModeMenuExpanded,
                        onDismissRequest = { loadModeMenuExpanded = false }
                    ) {
                        LoadMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.displayName()) },
                                onClick = {
                                    selectedLoadMode = mode
                                    loadModeMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val trimmedName = exerciseName.trim()

                    if (trimmedName.isNotEmpty()) {
                        onAddWorkoutEntry(
                            trimmedName,
                            selectedType,
                            if (selectedType == WorkoutEntryType.STRENGTH) {
                                selectedLoadMode
                            } else {
                                LoadMode.TOTAL
                            }
                        )

                        exerciseName = ""
                        selectedType = WorkoutEntryType.STRENGTH
                        selectedLoadMode = LoadMode.TOTAL
                        typeMenuExpanded = false
                        loadModeMenuExpanded = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add entry")
            }
        }
    }
}

private fun WorkoutEntryType.displayName(): String {
    return when (this) {
        WorkoutEntryType.STRENGTH -> "Strength"
        WorkoutEntryType.CARDIO -> "Cardio"
        WorkoutEntryType.CIRCUIT -> "Circuit"
        WorkoutEntryType.CIRCUIT_ROUND -> "Circuit round"
        WorkoutEntryType.STRETCHING -> "Stretching"
    }
}

private fun LoadMode.displayName(): String {
    return when (this) {
        LoadMode.TOTAL -> "Total"
        LoadMode.PER_MEMBER -> "Per member"
    }
}