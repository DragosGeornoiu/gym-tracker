package com.dragos.geornoiu.gymtracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import com.dragos.geornoiu.gymtracker.domain.ConfigOption
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition
import com.dragos.geornoiu.gymtracker.domain.LoadMode
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType
import com.dragos.geornoiu.gymtracker.ui.screens.exercise.DeleteExerciseDialog
import com.dragos.geornoiu.gymtracker.ui.screens.exercise.EditExerciseDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    exerciseDefinitions: List<ExerciseDefinition>,
    equipmentTypes: List<ConfigOption>,
    onBackClick: () -> Unit,
    onAddExerciseClick: (String, WorkoutEntryType, LoadMode, Long?) -> Unit,
    onUpdateExerciseClick: (ExerciseDefinition) -> Unit,
    onDeleteExerciseClick: (ExerciseDefinition, () -> Unit) -> Unit,
    onConfigureEquipmentTypesClick: () -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(WorkoutEntryType.STRENGTH) }
    var selectedLoadMode by remember { mutableStateOf(LoadMode.TOTAL) }
    var selectedEquipmentType by remember { mutableStateOf<ConfigOption?>(null) }

    var typeMenuExpanded by remember { mutableStateOf(false) }
    var loadModeMenuExpanded by remember { mutableStateOf(false) }
    var equipmentMenuExpanded by remember { mutableStateOf(false) }

    var lastAddedExerciseName by remember { mutableStateOf<String?>(null) }

    val equipmentById = equipmentTypes.associateBy { it.id }

    var exercisePendingEdit by remember { mutableStateOf<ExerciseDefinition?>(null) }
    var exercisePendingDelete by remember { mutableStateOf<ExerciseDefinition?>(null) }

    var blockedDeleteExercise by remember { mutableStateOf<ExerciseDefinition?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercise library") },
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
                    text = "Add exercise",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = {
                        nameText = it
                        lastAddedExerciseName = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Exercise name") },
                    singleLine = true
                )
            }

            item {
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
                        label = { Text("Default type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(typeMenuExpanded)
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
            }

            if (selectedType == WorkoutEntryType.STRENGTH) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = loadModeMenuExpanded,
                        onExpandedChange = {
                            loadModeMenuExpanded = !loadModeMenuExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedLoadMode.displayName(),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            label = { Text("Default load mode") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    loadModeMenuExpanded
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = loadModeMenuExpanded,
                            onDismissRequest = { loadModeMenuExpanded = false }
                        ) {
                            LoadMode.entries.forEach { loadMode ->
                                DropdownMenuItem(
                                    text = { Text(loadMode.displayName()) },
                                    onClick = {
                                        selectedLoadMode = loadMode
                                        loadModeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = equipmentMenuExpanded,
                    onExpandedChange = {
                        equipmentMenuExpanded = !equipmentMenuExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedEquipmentType?.label ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Equipment type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                equipmentMenuExpanded
                            )
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = equipmentMenuExpanded,
                        onDismissRequest = { equipmentMenuExpanded = false }
                    ) {
                        equipmentTypes.forEach { equipment ->
                            DropdownMenuItem(
                                text = { Text(equipment.label) },
                                onClick = {
                                    selectedEquipmentType = equipment
                                    equipmentMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                TextButton(
                    onClick = onConfigureEquipmentTypesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Configure equipment types")
                }
            }

            item {
                Button(
                    onClick = {
                        val name = nameText.trim()

                        if (name.isNotBlank()) {
                            onAddExerciseClick(
                                name,
                                selectedType,
                                if (selectedType == WorkoutEntryType.STRENGTH) {
                                    selectedLoadMode
                                } else {
                                    LoadMode.TOTAL
                                },
                                selectedEquipmentType?.id
                            )

                            lastAddedExerciseName = name
                            nameText = ""
                            selectedType = WorkoutEntryType.STRENGTH
                            selectedLoadMode = LoadMode.TOTAL
                            selectedEquipmentType = null
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add exercise")
                }
            }

            lastAddedExerciseName?.let { addedName ->
                item {
                    Text(
                        text = "Added: $addedName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Text(
                    text = "Available exercises",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (exerciseDefinitions.isEmpty()) {
                item {
                    Text("No exercises yet.")
                }
            } else {
                items(exerciseDefinitions) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
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

                            if (exercise.defaultType == WorkoutEntryType.STRENGTH) {
                                Text(
                                    text = "Load: ${exercise.defaultLoadMode.displayName()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            val equipmentLabel = exercise.equipmentTypeOptionId
                                ?.let { equipmentById[it]?.label }

                            if (equipmentLabel != null) {
                                Text(
                                    text = "Equipment: $equipmentLabel",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Text(
                                text = if (exercise.isBuiltIn) "Built-in" else "Custom",
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = { exercisePendingEdit = exercise }
                            ) {
                                Text("Edit")
                            }

                            TextButton(
                                onClick = { exercisePendingDelete = exercise }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    exercisePendingEdit?.let { exercise ->
        EditExerciseDialog(
            exercise = exercise,
            equipmentTypes = equipmentTypes,
            onDismiss = {
                exercisePendingEdit = null
            },
            onSave = { updatedExercise ->
                onUpdateExerciseClick(updatedExercise)
                exercisePendingEdit = null
            }
        )
    }

    exercisePendingDelete?.let { exercise ->
        DeleteExerciseDialog(
            exercise = exercise,
            onDismiss = {
                exercisePendingDelete = null
            },
            onConfirmDelete = {
                onDeleteExerciseClick(it) {
                    blockedDeleteExercise = it
                }
                exercisePendingDelete = null
            }
        )
    }

    blockedDeleteExercise?.let { exercise ->
        AlertDialog(
            onDismissRequest = {
                blockedDeleteExercise = null
            },
            title = {
                Text("Cannot delete exercise")
            },
            text = {
                Text("${exercise.name} is already used in workout entries. Later we will add a replace flow.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        blockedDeleteExercise = null
                    }
                ) {
                    Text("OK")
                }
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