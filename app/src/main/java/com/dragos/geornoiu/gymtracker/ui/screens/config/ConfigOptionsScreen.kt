package com.dragos.geornoiu.gymtracker.ui.screens.config

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.dragos.geornoiu.gymtracker.ui.components.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigOptionsScreen(
    title: String,
    options: List<ConfigOption>,
    onBackClick: () -> Unit,
    onAddOptionClick: (String) -> Unit,
    onUpdateOptionClick: (ConfigOption) -> Unit,
    onDeleteOptionClick: (ConfigOption, () -> Unit) -> Unit
) {
    var labelText by remember { mutableStateOf("") }
    var optionPendingEdit by remember { mutableStateOf<ConfigOption?>(null) }
    var optionPendingDelete by remember { mutableStateOf<ConfigOption?>(null) }
    var blockedDeleteOption by remember { mutableStateOf<ConfigOption?>(null) }
    var lastAddedOptionLabel by remember { mutableStateOf<String?>(null) }
    var optionPendingUpdate by remember { mutableStateOf<ConfigOption?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                    text = "Add option",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OutlinedTextField(
                    value = labelText,
                    onValueChange = {
                        labelText = it
                        lastAddedOptionLabel = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Option name") },
                    singleLine = true
                )
            }

            item {
                Button(
                    onClick = {
                        val label = labelText.trim()

                        if (label.isNotBlank()) {
                            onAddOptionClick(label)
                            lastAddedOptionLabel = label
                            labelText = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add option")
                }
            }

            lastAddedOptionLabel?.let { label ->
                item {
                    Text(
                        text = "Added: $label",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Text(
                    text = "Available options",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (options.isEmpty()) {
                item {
                    Text("No options yet.")
                }
            } else {
                items(options) { option ->
                    Card(
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
                                text = option.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = if (option.isBuiltIn) "Built-in" else "Custom",
                                style = MaterialTheme.typography.bodySmall
                            )

                            TextButton(
                                onClick = {
                                    optionPendingEdit = option
                                }
                            ) {
                                Text("Edit")
                            }

                            TextButton(
                                onClick = {
                                    optionPendingDelete = option
                                }
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    optionPendingEdit?.let { option ->
        EditConfigOptionDialog(
            option = option,
            onDismiss = {
                optionPendingEdit = null
            },
            onSave = { updatedOption ->
                optionPendingUpdate = updatedOption
                optionPendingEdit = null
            }
        )
    }

    optionPendingUpdate?.let { option ->
        ConfirmationDialog(
            title = "Update option?",
            message = "Save changes to ${option.label}?",
            confirmText = "Save",
            onConfirm = {
                onUpdateOptionClick(option)
                optionPendingUpdate = null
            },
            onDismiss = {
                optionPendingUpdate = null
            }
        )
    }

    optionPendingDelete?.let { option ->
        ConfirmationDialog(
            title = "Delete option?",
            message = "${option.label} will be removed from this configuration list.",
            confirmText = "Delete",
            onConfirm = {
                onDeleteOptionClick(option) {
                    blockedDeleteOption = option
                }
                optionPendingDelete = null
            },
            onDismiss = {
                optionPendingDelete = null
            }
        )
    }

    blockedDeleteOption?.let { option ->
        AlertDialog(
            onDismissRequest = {
                blockedDeleteOption = null
            },
            title = {
                Text("Cannot delete option")
            },
            text = {
                Text("${option.label} is currently used. Change existing records to another option before deleting it.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        blockedDeleteOption = null
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}