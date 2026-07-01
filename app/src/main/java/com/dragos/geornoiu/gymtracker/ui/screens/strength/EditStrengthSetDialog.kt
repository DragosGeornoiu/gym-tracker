package com.dragos.geornoiu.gymtracker.ui.screens.strength

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.ConfigOption
import com.dragos.geornoiu.gymtracker.domain.EffortRating
import com.dragos.geornoiu.gymtracker.domain.StrengthSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStrengthSetDialog(
    set: StrengthSet,
    effortOptions: List<ConfigOption>,
    onDismiss: () -> Unit,
    onSave: (StrengthSet) -> Unit
) {
    var weightText by remember(set.id) { mutableStateOf(set.weightKg?.toString() ?: "") }
    var repsText by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }
    var isWarmup by remember(set.id) { mutableStateOf(set.isWarmup) }
    var selectedEffortOption by remember(set.id) {
        mutableStateOf(
            effortOptions.firstOrNull { it.id == set.effortRatingOptionId }
        )
    }
    var noteText by remember(set.id) { mutableStateOf(set.note) }
    var effortMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit set")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Weight kg") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = repsText,
                        onValueChange = { repsText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Reps") },
                        singleLine = true
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isWarmup,
                        onCheckedChange = { isWarmup = it }
                    )

                    Text("Warmup")
                }

                ExposedDropdownMenuBox(
                    expanded = effortMenuExpanded,
                    onExpandedChange = { effortMenuExpanded = !effortMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEffortOption?.label ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Effort") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = effortMenuExpanded
                            )
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = effortMenuExpanded,
                        onDismissRequest = { effortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedEffortOption = null
                                effortMenuExpanded = false
                            }
                        )

                        effortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    selectedEffortOption = option
                                    effortMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        set.copy(
                            weightKg = weightText.toDoubleOrNull(),
                            reps = repsText.toIntOrNull(),
                            isWarmup = isWarmup,
                            effortRatingOptionId = selectedEffortOption?.id,
                            note = noteText.trim()
                        )
                    )
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