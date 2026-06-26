package com.dragos.geornoiu.gymtracker.ui.screens.strength

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dragos.geornoiu.gymtracker.domain.StrengthSet

@Composable
fun StrengthSetCard(
    setNumber: Int,
    set: StrengthSet,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUpClick: () -> Unit,
    onMoveDownClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
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
                text = "Set $setNumber",
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = buildString {
                    if (set.weightKg != null) append("${set.weightKg}kg")
                    if (set.weightKg != null && set.reps != null) append(" x ")
                    if (set.reps != null) append("${set.reps} reps")
                }.ifBlank { "No weight/reps" }
            )

            if (set.isWarmup) {
                Text("Warmup")
            }

            if (set.effortRating != null) {
                Text("Effort: ${set.effortRating.name}")
            }

            if (set.note.isNotBlank()) {
                Text("Note: ${set.note}")
            }

            TextButton(onClick = onEditClick) {
                Text("Edit")
            }

            TextButton(onClick = onDeleteClick) {
                Text("Delete")
            }

            TextButton(
                onClick = onMoveUpClick,
                enabled = canMoveUp
            ) {
                Text("Up")
            }

            TextButton(
                onClick = onMoveDownClick,
                enabled = canMoveDown
            ) {
                Text("Down")
            }
        }
    }
}