package com.dragos.geornoiu.gymtracker.ui.screens.strength

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.dragos.geornoiu.gymtracker.domain.StrengthSet

@Composable
fun DeleteStrengthSetDialog(
    set: StrengthSet,
    onDismiss: () -> Unit,
    onConfirmDelete: (StrengthSet) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete set?")
        },
        text = {
            Text("This set will be permanently removed.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete(set)
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}