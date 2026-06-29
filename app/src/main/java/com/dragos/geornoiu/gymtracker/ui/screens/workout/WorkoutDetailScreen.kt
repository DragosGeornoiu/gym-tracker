package com.dragos.geornoiu.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition
import com.dragos.geornoiu.gymtracker.domain.LoadMode
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntry
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.dragos.geornoiu.gymtracker.ui.components.ConfirmationDialog

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WorkoutDetailScreen(
    workoutId: Long,
    workoutTitle: String,
    workoutEntries: List<WorkoutEntry>,
    exerciseDefinitions: List<ExerciseDefinition>,
    onBackClick: () -> Unit,
    onAddWorkoutEntry: (Long?, String, WorkoutEntryType, LoadMode) -> Unit,
    onWorkoutEntryClick: (Long) -> Unit,
    onDeleteWorkoutEntryClick: (WorkoutEntry) -> Unit,
    onManageExercisesClick: () -> Unit,
    onUpdateWorkoutEntryExerciseClick: (WorkoutEntry, ExerciseDefinition) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedExercise by remember { mutableStateOf<ExerciseDefinition?>(null) }
    var showExercisePicker by remember { mutableStateOf(false) }
    var entryPendingDelete by remember { mutableStateOf<WorkoutEntry?>(null) }
    var entryPendingEdit by remember { mutableStateOf<WorkoutEntry?>(null) }
    var pendingReplacementExercise by remember { mutableStateOf<ExerciseDefinition?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Workout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = workoutTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Text(
                    text = "Exercise entries",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (workoutEntries.isEmpty()) {
                item {
                    Text("No exercise entries yet.")
                }
            } else {
                items(workoutEntries) { entry ->
                    Card(
                        onClick = { onWorkoutEntryClick(entry.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
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

                            var menuExpanded by remember { mutableStateOf(false) }

                            TextButton(
                                onClick = { menuExpanded = true }
                            ) {
                                Text("⋮")
                            }

                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit exercise") },
                                    onClick = {
                                        menuExpanded = false
                                        entryPendingEdit = entry
                                        showExercisePicker = true
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        menuExpanded = false
                                        entryPendingDelete = entry
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Add entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Card(
                    onClick = { showExercisePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Exercise",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = selectedExercise?.name ?: "Select exercise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        selectedExercise?.let { exercise ->
                            Text(
                                text = exercise.defaultType.displayName(),
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (exercise.defaultType == WorkoutEntryType.STRENGTH) {
                                Text(
                                    text = "Load: ${exercise.defaultLoadMode.displayName()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        selectedExercise?.let { exercise ->
                            onAddWorkoutEntry(
                                exercise.id,
                                exercise.name,
                                exercise.defaultType,
                                exercise.defaultLoadMode
                            )

                            selectedExercise = null
                        }
                    },
                    enabled = selectedExercise != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add entry")
                }
            }

            item {
                TextButton(
                    onClick = onManageExercisesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage exercises")
                }
            }
        }
    }

    if (showExercisePicker) {
        ExercisePickerDialog(
            exercises = exerciseDefinitions,
            onDismiss = {
                showExercisePicker = false
                entryPendingEdit = null
                pendingReplacementExercise = null
            },
            onExerciseSelected = { exercise ->
                val editingEntry = entryPendingEdit

                if (editingEntry != null) {
                    pendingReplacementExercise = exercise
                } else {
                    selectedExercise = exercise
                }

                showExercisePicker = false
            },
            onManageExercisesClick = {
                showExercisePicker = false
                onManageExercisesClick()
            }
        )
    }

    entryPendingDelete?.let { entry ->
        ConfirmationDialog(
            title = "Delete workout entry?",
            message = "This will remove ${entry.name} from this workout.",
            confirmText = "Delete",
            onConfirm = {
                onDeleteWorkoutEntryClick(entry)
                entryPendingDelete = null
            },
            onDismiss = {
                entryPendingDelete = null
            }
        )
    }

    val editingEntry = entryPendingEdit
    val replacementExercise = pendingReplacementExercise

    if (editingEntry != null && replacementExercise != null) {
        ConfirmationDialog(
            title = "Replace exercise?",
            message = "${editingEntry.name} will be replaced with ${replacementExercise.name}. Existing details may no longer match the new exercise type.",
            confirmText = "Replace",
            onConfirm = {
                onUpdateWorkoutEntryExerciseClick(editingEntry, replacementExercise)
                entryPendingEdit = null
                pendingReplacementExercise = null
            },
            onDismiss = {
                entryPendingEdit = null
                pendingReplacementExercise = null
            }
        )
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