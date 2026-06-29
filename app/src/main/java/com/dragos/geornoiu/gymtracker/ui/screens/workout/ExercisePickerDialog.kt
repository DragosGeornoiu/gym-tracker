package com.dragos.geornoiu.gymtracker.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType

@Composable
fun ExercisePickerDialog(
    exercises: List<ExerciseDefinition>,
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseDefinition) -> Unit,
    onManageExercisesClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val filteredExercises = exercises.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select exercise")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search") },
                    singleLine = true
                )

                if (filteredExercises.isEmpty()) {
                    Text("No exercise found.")
                    TextButton(onClick = onManageExercisesClick) {
                        Text("Add new exercise")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredExercises) { exercise ->
                            Card(
                                onClick = {
                                    onExerciseSelected(exercise)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = exercise.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = exercise.defaultType.displayName(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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