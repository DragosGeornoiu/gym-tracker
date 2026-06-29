package com.dragos.geornoiu.gymtracker.ui.screens.exercise

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition

@Composable
fun DeleteExerciseDialog(
    exercise: ExerciseDefinition,
    onDismiss: () -> Unit,
    onConfirmDelete: (ExerciseDefinition) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete exercise?")
        },
        text = {
            Text("This exercise will be removed from the exercise picker.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete(exercise)
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