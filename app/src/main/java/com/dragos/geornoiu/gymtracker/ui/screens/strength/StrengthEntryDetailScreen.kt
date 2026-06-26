package com.dragos.geornoiu.gymtracker.ui.screens.strength

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.EffortRating
import com.dragos.geornoiu.gymtracker.domain.StrengthSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthEntryDetailScreen(
    workoutEntryId: Long,
    entryName: String,
    strengthSets: List<StrengthSet>,
    onBackClick: () -> Unit,
    onAddSetClick: (
        weightKg: Double?,
        reps: Int?,
        isWarmup: Boolean,
        effortRating: EffortRating?,
        note: String
    ) -> Unit,
    onUpdateSetClick: (StrengthSet) -> Unit,
    onDeleteSetClick: (StrengthSet) -> Unit,
    onMoveSetClick: (StrengthSet, Int) -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var isWarmup by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }
    var selectedEffort by remember { mutableStateOf<EffortRating?>(null) }
    var effortMenuExpanded by remember { mutableStateOf(false) }
    var setPendingDelete by remember { mutableStateOf<StrengthSet?>(null) }
    var setPendingEdit by remember { mutableStateOf<StrengthSet?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entryName) },
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
                    text = "Sets",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (strengthSets.isEmpty()) {
                item {
                    Text("No sets added yet.")
                }
            } else {
                items(strengthSets.withIndex().toList()) { indexedSet ->
                    StrengthSetCard(
                        setNumber = indexedSet.index + 1,
                        set = indexedSet.value,
                        canMoveUp = indexedSet.index > 0,
                        canMoveDown = indexedSet.index < strengthSets.lastIndex,
                        onMoveUpClick = { onMoveSetClick(indexedSet.value, -1) },
                        onMoveDownClick = { onMoveSetClick(indexedSet.value, 1) },
                        onDeleteClick = { setPendingDelete = indexedSet.value },
                        onEditClick = { setPendingEdit = indexedSet.value }
                    )
                }
            }

            item {
                Text(
                    text = "Add set",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
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
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isWarmup,
                        onCheckedChange = { isWarmup = it }
                    )

                    Text("Warmup")
                }
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = effortMenuExpanded,
                    onExpandedChange = { effortMenuExpanded = !effortMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEffort?.name ?: "",
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
                        EffortRating.entries.forEach { rating ->
                            DropdownMenuItem(
                                text = { Text(rating.name) },
                                onClick = {
                                    selectedEffort = rating
                                    effortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note") }
                )
            }

            item {
                Button(
                    onClick = {
                        val weight = weightText.toDoubleOrNull()
                        val reps = repsText.toIntOrNull()

                        if (weight != null || reps != null || noteText.isNotBlank()) {
                            onAddSetClick(
                                weight,
                                reps,
                                isWarmup,
                                selectedEffort,
                                noteText.trim()
                            )

                            weightText = ""
                            repsText = ""
                            isWarmup = false
                            selectedEffort = null
                            noteText = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add set")
                }
            }
        }
    }

    setPendingDelete?.let { set ->
        DeleteStrengthSetDialog(
            set = set,
            onDismiss = {
                setPendingDelete = null
            },
            onConfirmDelete = {
                onDeleteSetClick(it)
                setPendingDelete = null
            }
        )
    }

    setPendingEdit?.let { set ->
        EditStrengthSetDialog(
            set = set,
            onDismiss = {
                setPendingEdit = null
            },
            onSave = { updatedSet ->
                onUpdateSetClick(updatedSet)
                setPendingEdit = null
            }
        )
    }
}