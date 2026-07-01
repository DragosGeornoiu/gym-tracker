package com.dragos.geornoiu.gymtracker.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onExercisesClick: () -> Unit,
    onEquipmentTypesClick: () -> Unit,
    onCardioTargetTypesClick: () -> Unit,
    onEffortRatingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsRow(
                title = "Exercises",
                subtitle = "Manage exercise library",
                onClick = onExercisesClick
            )

            SettingsRow(
                title = "Equipment types",
                subtitle = "Manage equipment options",
                onClick = onEquipmentTypesClick
            )

            SettingsRow(
                title = "Cardio target types",
                subtitle = "Manage cardio target options",
                onClick = onCardioTargetTypesClick
            )

            SettingsRow(
                title = "Effort ratings",
                subtitle = "Manage effort options",
                onClick = onEffortRatingsClick
            )
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}