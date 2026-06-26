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
import com.dragos.geornoiu.gymtracker.ui.screens.strength.StrengthEntryDetailScreen
import com.dragos.geornoiu.gymtracker.ui.screens.WorkoutDetailScreen
import com.dragos.geornoiu.gymtracker.ui.screens.WorkoutHistoryScreen
import com.dragos.geornoiu.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var selectedWorkoutId by mutableStateOf<Long?>(null)
    private var selectedWorkoutTitle by mutableStateOf<String?>(null)
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
                                    val newWorkoutId = workoutRepository.createTodayDraftWorkout()

                                    selectedWorkoutId = newWorkoutId
                                    selectedWorkoutTitle = "Workout ${java.time.LocalDate.now()}"
                                }
                            },
                            onWorkoutClick = { workoutId ->
                                val workout = workouts.firstOrNull { it.id == workoutId }

                                selectedWorkoutId = workoutId
                                selectedWorkoutTitle = workout?.title
                            }
                        )
                    }

                    selectedWorkoutEntryId != null && selectedWorkoutEntryType != null -> {
                        val entryId = selectedWorkoutEntryId!!
                        val entryType = selectedWorkoutEntryType!!

                        when (entryType) {
                            WorkoutEntryType.STRENGTH -> {
                                val strengthSets by workoutRepository
                                    .observeStrengthSets(entryId)
                                    .collectAsState(initial = emptyList())

                                StrengthEntryDetailScreen(
                                    workoutEntryId = entryId,
                                    entryName = selectedWorkoutEntryName ?: "Strength",
                                    strengthSets = strengthSets,
                                    onBackClick = {
                                        selectedWorkoutEntryId = null
                                        selectedWorkoutEntryType = null
                                        selectedWorkoutEntryName = null
                                    },
                                    onAddSetClick = { weightKg, reps, isWarmup, effortRating, note ->
                                        lifecycleScope.launch {
                                            workoutRepository.addStrengthSet(
                                                workoutEntryId = entryId,
                                                weightKg = weightKg,
                                                reps = reps,
                                                isWarmup = isWarmup,
                                                effortRating = effortRating,
                                                note = note
                                            )
                                        }
                                    },
                                    onUpdateSetClick = { set ->
                                        lifecycleScope.launch {
                                            workoutRepository.updateStrengthSet(set)
                                        }
                                    },
                                    onDeleteSetClick = { set ->
                                        lifecycleScope.launch {
                                            workoutRepository.deleteStrengthSet(set)
                                        }
                                    },
                                    onMoveSetClick = { set, direction ->
                                        lifecycleScope.launch {
                                            workoutRepository.moveStrengthSet(
                                                sets = strengthSets,
                                                setId = set.id,
                                                direction = direction
                                            )
                                        }
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
                            workoutTitle = selectedWorkoutTitle ?: "Workout",
                            workoutEntries = workoutEntries,
                            onBackClick = {
                                selectedWorkoutId = null
                                selectedWorkoutTitle = null
                                selectedWorkoutEntryId = null
                                selectedWorkoutEntryType = null
                                selectedWorkoutEntryName = null
                            },
                            onAddWorkoutEntry = { name, type, loadMode ->
                                lifecycleScope.launch {
                                    workoutRepository.addWorkoutEntry(
                                        workoutId = workoutId,
                                        name = name,
                                        type = type,
                                        loadMode = loadMode
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