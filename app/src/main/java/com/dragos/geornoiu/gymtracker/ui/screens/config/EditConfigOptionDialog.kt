package com.dragos.geornoiu.gymtracker.ui.screens.config

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dragos.geornoiu.gymtracker.domain.ConfigOption

@Composable
fun EditConfigOptionDialog(
    option: ConfigOption,
    onDismiss: () -> Unit,
    onSave: (ConfigOption) -> Unit
) {
    var labelText by remember(option.id) { mutableStateOf(option.label) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit option") },
        text = {
            OutlinedTextField(
                value = labelText,
                onValueChange = { labelText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Option name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val label = labelText.trim()
                    if (label.isNotBlank()) {
                        onSave(option.copy(label = label))
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