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
import com.dragos.geornoiu.gymtracker.ui.screens.workout.WorkoutDetailScreen
import com.dragos.geornoiu.gymtracker.ui.screens.WorkoutHistoryScreen
import com.dragos.geornoiu.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.coroutines.launch
import com.dragos.geornoiu.gymtracker.ui.screens.ExerciseLibraryScreen
import com.dragos.geornoiu.gymtracker.domain.ConfigGroupKey
import com.dragos.geornoiu.gymtracker.ui.screens.config.ConfigOptionsScreen

class MainActivity : ComponentActivity() {
    private var selectedWorkoutId by mutableStateOf<Long?>(null)
    private var selectedWorkoutTitle by mutableStateOf<String?>(null)
    private var selectedWorkoutEntryId by mutableStateOf<Long?>(null)
    private var selectedWorkoutEntryType by mutableStateOf<WorkoutEntryType?>(null)
    private var selectedWorkoutEntryName by mutableStateOf<String?>(null)
    private var showExerciseLibrary by mutableStateOf(false)

    private var showEquipmentTypesConfig by mutableStateOf(false)

    private var selectedWorkoutDate by mutableStateOf<String?>(null)
    private var selectedWorkoutNotes by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = GymTrackerDatabase.getDatabase(applicationContext)
        val workoutRepository = WorkoutRepository(database.workoutDao())
        lifecycleScope.launch {
            workoutRepository.seedDefaultExerciseDefinitionsIfNeeded()
            workoutRepository.seedDefaultConfigOptionsIfNeeded()
        }

        setContent {
            GymTrackerTheme {
                when {
                    showEquipmentTypesConfig -> {
                        val equipmentTypes by workoutRepository
                            .observeConfigOptions(ConfigGroupKey.EQUIPMENT_TYPE)
                            .collectAsState(initial = emptyList())

                        ConfigOptionsScreen(
                            title = "Equipment types",
                            options = equipmentTypes,
                            onBackClick = {
                                showEquipmentTypesConfig = false
                            },
                            onAddOptionClick = { label ->
                                lifecycleScope.launch {
                                    workoutRepository.addConfigOption(
                                        groupKey = ConfigGroupKey.EQUIPMENT_TYPE,
                                        label = label
                                    )
                                }
                            },
                            onUpdateOptionClick = { option ->
                                lifecycleScope.launch {
                                    workoutRepository.updateConfigOption(option)
                                }
                            },
                            onDeleteOptionClick = { option, onBlocked ->
                                lifecycleScope.launch {
                                    val canArchive = workoutRepository.canArchiveConfigOption(option)

                                    if (canArchive) {
                                        workoutRepository.archiveConfigOption(option.id)
                                    } else {
                                        onBlocked()
                                    }
                                }
                            }
                        )
                    }

                    showExerciseLibrary -> {
                        val exerciseDefinitions by workoutRepository
                            .observeExerciseDefinitions()
                            .collectAsState(initial = emptyList())

                        val equipmentTypes by workoutRepository
                            .observeConfigOptions(ConfigGroupKey.EQUIPMENT_TYPE)
                            .collectAsState(initial = emptyList())

                        ExerciseLibraryScreen(
                            exerciseDefinitions = exerciseDefinitions,
                            equipmentTypes = equipmentTypes,
                            onBackClick = {
                                showExerciseLibrary = false
                            },
                            onAddExerciseClick = { name, type, loadMode, equipmentTypeOptionId ->
                                lifecycleScope.launch {
                                    workoutRepository.addExerciseDefinition(
                                        name = name,
                                        defaultType = type,
                                        defaultLoadMode = loadMode,
                                        equipmentTypeOptionId = equipmentTypeOptionId
                                    )
                                }
                            },
                            onConfigureEquipmentTypesClick = {
                                showEquipmentTypesConfig = true
                            },

                            onUpdateExerciseClick = { exercise ->
                                lifecycleScope.launch {
                                    workoutRepository.updateExerciseDefinition(exercise)
                                }
                            },
                            onDeleteExerciseClick = { exercise, onBlocked ->
                                lifecycleScope.launch {
                                    val canArchive = workoutRepository.canArchiveExerciseDefinition(exercise.id)

                                    if (canArchive) {
                                        workoutRepository.archiveExerciseDefinition(exercise.id)
                                    } else {
                                        onBlocked()
                                    }
                                }
                            },
                        )
                    }


                    selectedWorkoutId == null -> {
                        val workouts by workoutRepository
                            .observeWorkoutSummaries()
                            .collectAsState(initial = emptyList())

                        WorkoutHistoryScreen(
                            workouts = workouts,
                            onAddWorkoutClick = {
                                lifecycleScope.launch {
                                    val today = java.time.LocalDate.now().toString()
                                    val newWorkoutId = workoutRepository.createTodayDraftWorkout()

                                    selectedWorkoutId = newWorkoutId
                                    selectedWorkoutTitle = "Workout $today"
                                    selectedWorkoutDate = today
                                    selectedWorkoutNotes = ""
                                }
                            },
                            onWorkoutClick = { workoutId ->
                                val workout = workouts.firstOrNull { it.id == workoutId }

                                selectedWorkoutId = workoutId
                                selectedWorkoutTitle = workout?.title
                                selectedWorkoutDate = workout?.date
                                selectedWorkoutNotes = ""
                            },
                            onDeleteWorkoutClick = { workout ->
                                lifecycleScope.launch {
                                    workoutRepository.deleteWorkout(workout.id)
                                }
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

                        val exerciseDefinitions by workoutRepository
                            .observeExerciseDefinitions()
                            .collectAsState(initial = emptyList())

                        WorkoutDetailScreen(
                            workoutId = workoutId,
                            workoutTitle = selectedWorkoutTitle ?: "Workout",
                            workoutEntries = workoutEntries,
                            exerciseDefinitions = exerciseDefinitions,
                            onBackClick = {
                                selectedWorkoutId = null
                                selectedWorkoutTitle = null
                                selectedWorkoutEntryId = null
                                selectedWorkoutEntryType = null
                                selectedWorkoutEntryName = null
                                selectedWorkoutDate = null
                                selectedWorkoutNotes = null
                            },
                            onAddWorkoutEntry = { exerciseDefinitionId, name, type, loadMode ->
                                lifecycleScope.launch {
                                    workoutRepository.addWorkoutEntry(
                                        workoutId = workoutId,
                                        exerciseDefinitionId = exerciseDefinitionId,
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
                            },
                            onManageExercisesClick = {
                                showExerciseLibrary = true
                            },
                            onDeleteWorkoutEntryClick = { entry ->
                                lifecycleScope.launch {
                                    workoutRepository.deleteWorkoutEntry(entry.id)
                                }
                            },
                            onUpdateWorkoutEntryExerciseClick = { entry, exercise ->
                                lifecycleScope.launch {
                                    workoutRepository.updateWorkoutEntryExercise(
                                        entry = entry,
                                        exerciseDefinition = exercise
                                    )
                                }
                            },
                            workoutDate = selectedWorkoutDate ?: "",
                            workoutNotes = selectedWorkoutNotes ?: "",
                            onUpdateWorkoutMetadataClick = { title, date, notes ->
                                lifecycleScope.launch {
                                    workoutRepository.updateWorkoutMetadata(
                                        id = workoutId,
                                        title = title,
                                        date = date,
                                        notes = notes
                                    )

                                    selectedWorkoutTitle = title
                                    selectedWorkoutDate = date
                                    selectedWorkoutNotes = notes
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}