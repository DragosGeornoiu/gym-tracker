package com.dragos.geornoiu.gymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.dragos.geornoiu.gymtracker.data.WorkoutRepository
import com.dragos.geornoiu.gymtracker.data.local.GymTrackerDatabase
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType
import com.dragos.geornoiu.gymtracker.ui.screens.EntryPlaceholderScreen
import com.dragos.geornoiu.gymtracker.ui.screens.StrengthEntryDetailScreen
import com.dragos.geornoiu.gymtracker.ui.screens.WorkoutDetailScreen
import com.dragos.geornoiu.gymtracker.ui.screens.WorkoutHistoryScreen
import com.dragos.geornoiu.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var selectedWorkoutId by mutableStateOf<Long?>(null)
    private var selectedWorkoutEntryId by mutableStateOf<Long?>(null)
    private var selectedWorkoutEntryType by mutableStateOf<WorkoutEntryType?>(null)
    private var selectedWorkoutEntryName by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = GymTrackerDatabase.getDatabase(applicationContext)
        val workoutRepository = WorkoutRepository(database.workoutDao())

        setContent {
            GymTrackerTheme {
                when {
                    selectedWorkoutId == null -> {
                        val workouts by workoutRepository
                            .observeWorkoutSummaries()
                            .collectAsState(initial = emptyList())

                        WorkoutHistoryScreen(
                            workouts = workouts,
                            onAddWorkoutClick = {
                                lifecycleScope.launch {
                                    selectedWorkoutId =
                                        workoutRepository.createTodayDraftWorkout()
                                }
                            },
                            onWorkoutClick = { workoutId ->
                                selectedWorkoutId = workoutId
                            }
                        )
                    }

                    selectedWorkoutEntryId != null && selectedWorkoutEntryType != null -> {
                        val entryId = selectedWorkoutEntryId!!
                        val entryType = selectedWorkoutEntryType!!

                        when (entryType) {
                            WorkoutEntryType.STRENGTH -> {
                                StrengthEntryDetailScreen(
                                    workoutEntryId = entryId,
                                    entryName = selectedWorkoutEntryName ?: "Strength",
                                    onBackClick = {
                                        selectedWorkoutEntryId = null
                                        selectedWorkoutEntryType = null
                                        selectedWorkoutEntryName = null
                                    }
                                )
                            }

                            else -> {
                                EntryPlaceholderScreen(
                                    title = selectedWorkoutEntryName ?: entryType.name,
                                    onBackClick = {
                                        selectedWorkoutEntryId = null
                                        selectedWorkoutEntryType = null
                                        selectedWorkoutEntryName = null
                                    }
                                )
                            }
                        }
                    }

                    else -> {
                        val workoutId = selectedWorkoutId!!

                        val workoutEntries by workoutRepository
                            .observeRootWorkoutEntries(workoutId)
                            .collectAsState(initial = emptyList())

                        WorkoutDetailScreen(
                            workoutId = workoutId,
                            workoutEntries = workoutEntries,
                            onBackClick = {
                                selectedWorkoutId = null
                                selectedWorkoutEntryId = null
                                selectedWorkoutEntryType = null
                                selectedWorkoutEntryName = null
                            },
                            onAddWorkoutEntry = { name, type ->
                                lifecycleScope.launch {
                                    workoutRepository.addWorkoutEntry(
                                        workoutId = workoutId,
                                        name = name,
                                        type = type
                                    )
                                }
                            },
                            onWorkoutEntryClick = { entryId ->
                                val entry = workoutEntries.firstOrNull { it.id == entryId }

                                selectedWorkoutEntryId = entryId
                                selectedWorkoutEntryType = entry?.type
                                selectedWorkoutEntryName = entry?.name
                            }
                        )
                    }
                }
            }
        }
    }
}