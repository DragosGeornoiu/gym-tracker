package com.dragos.geornoiu.gymtracker.ui.screens.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.ConfigOption
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition
import com.dragos.geornoiu.gymtracker.domain.LoadMode
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseDialog(
    exercise: ExerciseDefinition,
    equipmentTypes: List<ConfigOption>,
    onDismiss: () -> Unit,
    onSave: (ExerciseDefinition) -> Unit
) {
    var nameText by remember(exercise.id) { mutableStateOf(exercise.name) }
    var selectedType by remember(exercise.id) { mutableStateOf(exercise.defaultType) }
    var selectedLoadMode by remember(exercise.id) { mutableStateOf(exercise.defaultLoadMode) }
    var selectedEquipmentType by remember(exercise.id) {
        mutableStateOf(equipmentTypes.firstOrNull { it.id == exercise.equipmentTypeOptionId })
    }

    var typeMenuExpanded by remember { mutableStateOf(false) }
    var loadModeMenuExpanded by remember { mutableStateOf(false) }
    var equipmentMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit exercise")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
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
                        label = { Text("Default type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(typeMenuExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = typeMenuExpanded,
                        onDismissRequest = { typeMenuExpanded = false }
                    ) {
                        editableWorkoutEntryTypes().forEach { type ->
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
                                ExposedDropdownMenuDefaults.TrailingIcon(loadModeMenuExpanded)
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
                            ExposedDropdownMenuDefaults.TrailingIcon(equipmentMenuExpanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = equipmentMenuExpanded,
                        onDismissRequest = { equipmentMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedEquipmentType = null
                                equipmentMenuExpanded = false
                            }
                        )

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
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val name = nameText.trim()

                    if (name.isNotBlank()) {
                        onSave(
                            exercise.copy(
                                name = name,
                                defaultType = selectedType,
                                defaultLoadMode = if (selectedType == WorkoutEntryType.STRENGTH) {
                                    selectedLoadMode
                                } else {
                                    LoadMode.TOTAL
                                },
                                equipmentTypeOptionId = selectedEquipmentType?.id
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun editableWorkoutEntryTypes(): List<WorkoutEntryType> {
    return listOf(
        WorkoutEntryType.STRENGTH,
        WorkoutEntryType.CARDIO,
        WorkoutEntryType.CIRCUIT,
        WorkoutEntryType.STRETCHING
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

private fun LoadMode.displayName(): String {
    return when (this) {
        LoadMode.TOTAL -> "Total"
        LoadMode.PER_MEMBER -> "Per member"
    }
}