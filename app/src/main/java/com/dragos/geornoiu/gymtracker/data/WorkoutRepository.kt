package com.dragos.geornoiu.gymtracker.data

import com.dragos.geornoiu.gymtracker.data.local.WorkoutDao
import com.dragos.geornoiu.gymtracker.data.local.entities.ExerciseDefinitionEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntryEntity
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntry
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType
import com.dragos.geornoiu.gymtracker.domain.WorkoutSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import com.dragos.geornoiu.gymtracker.data.local.entities.StrengthSetEntity
import com.dragos.geornoiu.gymtracker.domain.EffortRating
import com.dragos.geornoiu.gymtracker.domain.ExerciseDefinition
import com.dragos.geornoiu.gymtracker.domain.StrengthSet
import com.dragos.geornoiu.gymtracker.domain.LoadMode
import com.dragos.geornoiu.gymtracker.data.local.entities.ConfigOptionEntity
import com.dragos.geornoiu.gymtracker.domain.ConfigGroupKey
import com.dragos.geornoiu.gymtracker.domain.ConfigOption

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {

    fun observeWorkoutSummaries(): Flow<List<WorkoutSummary>> {
        return workoutDao.observeWorkouts()
            .map { workouts ->
                workouts.map { it.toSummary() }
            }
    }

    fun observeRootWorkoutEntries(workoutId: Long): Flow<List<WorkoutEntry>> {
        return workoutDao.observeRootWorkoutEntries(workoutId)
            .map { entries ->
                entries.map { it.toDomain() }
            }
    }

    suspend fun createTodayDraftWorkout(): Long {
        val today = LocalDate.now().toString()
        val now = System.currentTimeMillis()

        return workoutDao.insertWorkout(
            WorkoutEntity(
                date = today,
                title = "Workout $today",
                status = "DRAFT",
                notes = "",
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun addWorkoutEntry(
        workoutId: Long,
        name: String,
        type: WorkoutEntryType,
        loadMode: LoadMode = LoadMode.TOTAL,
        isWarmup: Boolean = false,
        note: String = ""
    ): Long {
        val orderIndex = workoutDao.getRootWorkoutEntryCount(workoutId)

        return workoutDao.insertWorkoutEntry(
            WorkoutEntryEntity(
                workoutId = workoutId,
                parentEntryId = null,
                type = type.name,
                name = name,
                orderIndex = orderIndex,
                isWarmup = isWarmup,
                loadMode = loadMode.name,
                note = note
            )
        )
    }

    private fun WorkoutEntity.toSummary(): WorkoutSummary {
        return WorkoutSummary(
            id = id,
            date = date,
            title = title,
            exerciseCount = 0,
            summary = if (status == "DRAFT") {
                "Workout in progress"
            } else {
                notes.ifBlank { "Completed workout" }
            }
        )
    }

    private fun WorkoutEntryEntity.toDomain(): WorkoutEntry {
        return WorkoutEntry(
            id = id,
            workoutId = workoutId,
            parentEntryId = parentEntryId,
            type = WorkoutEntryType.valueOf(type),
            name = name,
            orderIndex = orderIndex,
            isWarmup = isWarmup,
            loadMode = LoadMode.valueOf(loadMode),
            note = note
        )
    }

    fun observeStrengthSets(workoutEntryId: Long): Flow<List<StrengthSet>> {
        return workoutDao.observeStrengthSets(workoutEntryId)
            .map { sets ->
                sets.map { it.toDomain() }
            }
    }

    suspend fun addStrengthSet(
        workoutEntryId: Long,
        weightKg: Double?,
        reps: Int?,
        isWarmup: Boolean,
        effortRating: EffortRating?,
        note: String
    ): Long {
        val orderIndex = workoutDao.getStrengthSetCount(workoutEntryId)

        return workoutDao.insertStrengthSet(
            StrengthSetEntity(
                workoutEntryId = workoutEntryId,
                orderIndex = orderIndex,
                weightKg = weightKg,
                reps = reps,
                isWarmup = isWarmup,
                effortRating = effortRating?.name,
                note = note
            )
        )
    }

    private fun StrengthSetEntity.toDomain(): StrengthSet {
        return StrengthSet(
            id = id,
            workoutEntryId = workoutEntryId,
            orderIndex = orderIndex,
            weightKg = weightKg,
            reps = reps,
            isWarmup = isWarmup,
            effortRating = effortRating?.let { EffortRating.valueOf(it) },
            note = note
        )
    }

    suspend fun deleteStrengthSet(set: StrengthSet) {
        workoutDao.deleteStrengthSet(
            StrengthSetEntity(
                id = set.id,
                workoutEntryId = set.workoutEntryId,
                orderIndex = set.orderIndex,
                weightKg = set.weightKg,
                reps = set.reps,
                isWarmup = set.isWarmup,
                effortRating = set.effortRating?.name,
                note = set.note
            )
        )
    }

    suspend fun moveStrengthSet(
        sets: List<StrengthSet>,
        setId: Long,
        direction: Int
    ) {
        val currentIndex = sets.indexOfFirst { it.id == setId }
        if (currentIndex == -1) return

        val newIndex = currentIndex + direction
        if (newIndex !in sets.indices) return

        val reordered = sets.toMutableList()
        val moved = reordered.removeAt(currentIndex)
        reordered.add(newIndex, moved)

        reordered.forEachIndexed { index, set ->
            workoutDao.updateStrengthSetOrder(
                id = set.id,
                orderIndex = index
            )
        }
    }

    suspend fun updateStrengthSet(set: StrengthSet) {
        workoutDao.updateStrengthSet(
            StrengthSetEntity(
                id = set.id,
                workoutEntryId = set.workoutEntryId,
                orderIndex = set.orderIndex,
                weightKg = set.weightKg,
                reps = set.reps,
                isWarmup = set.isWarmup,
                effortRating = set.effortRating?.name,
                note = set.note
            )
        )
    }

    fun observeExerciseDefinitions(): Flow<List<ExerciseDefinition>> {
        return workoutDao.observeExerciseDefinitions()
            .map { definitions ->
                definitions.map { it.toDomain() }
            }
    }

    suspend fun addExerciseDefinition(
        name: String,
        defaultType: WorkoutEntryType,
        defaultLoadMode: LoadMode
    ): Long {
        return workoutDao.insertExerciseDefinition(
            ExerciseDefinitionEntity(
                name = name,
                defaultType = defaultType.name,
                defaultLoadMode = defaultLoadMode.name,
                isBuiltIn = false,
                isArchived = false
            )
        )
    }

    suspend fun seedDefaultExerciseDefinitionsIfNeeded() {
        if (workoutDao.getExerciseDefinitionCount() > 0) {
            return
        }

        val defaults = listOf(
            ExerciseDefinitionEntity(
                name = "Biceps curl",
                defaultType = WorkoutEntryType.STRENGTH.name,
                defaultLoadMode = LoadMode.PER_MEMBER.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "Triceps extension",
                defaultType = WorkoutEntryType.STRENGTH.name,
                defaultLoadMode = LoadMode.PER_MEMBER.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "RDL",
                defaultType = WorkoutEntryType.STRENGTH.name,
                defaultLoadMode = LoadMode.TOTAL.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "Running",
                defaultType = WorkoutEntryType.CARDIO.name,
                defaultLoadMode = LoadMode.TOTAL.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "Rowing",
                defaultType = WorkoutEntryType.CARDIO.name,
                defaultLoadMode = LoadMode.TOTAL.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "Bike",
                defaultType = WorkoutEntryType.CARDIO.name,
                defaultLoadMode = LoadMode.TOTAL.name,
                isBuiltIn = true
            ),
            ExerciseDefinitionEntity(
                name = "Stretching",
                defaultType = WorkoutEntryType.STRETCHING.name,
                defaultLoadMode = LoadMode.TOTAL.name,
                isBuiltIn = true
            )
        )

        workoutDao.insertExerciseDefinitions(defaults)
    }

    private fun ExerciseDefinitionEntity.toDomain(): ExerciseDefinition {
        return ExerciseDefinition(
            id = id,
            name = name,
            defaultType = WorkoutEntryType.valueOf(defaultType),
            defaultLoadMode = LoadMode.valueOf(defaultLoadMode),
            isBuiltIn = isBuiltIn,
            isArchived = isArchived
        )
    }
    fun observeConfigOptions(groupKey: String): Flow<List<ConfigOption>> {
        return workoutDao.observeConfigOptions(groupKey)
            .map { options ->
                options.map { it.toDomain() }
            }
    }

    suspend fun addConfigOption(
        groupKey: String,
        label: String
    ): Long {
        val orderIndex = workoutDao.getConfigOptionCountForGroup(groupKey)

        return workoutDao.insertConfigOption(
            ConfigOptionEntity(
                groupKey = groupKey,
                label = label.trim(),
                isBuiltIn = false,
                isArchived = false,
                orderIndex = orderIndex
            )
        )
    }

    suspend fun seedDefaultConfigOptionsIfNeeded() {
        seedEquipmentTypesIfNeeded()
    }

    private suspend fun seedEquipmentTypesIfNeeded() {
        if (workoutDao.getConfigOptionCountForGroup(ConfigGroupKey.EQUIPMENT_TYPE) > 0) {
            return
        }

        val defaults = listOf(
            "Bodyweight",
            "Barbell",
            "Dumbbell",
            "Cable",
            "Machine",
            "Plate-loaded machine",
            "Sled",
            "Cardio machine",
            "Other"
        )

        workoutDao.insertConfigOptions(
            defaults.mapIndexed { index, label ->
                ConfigOptionEntity(
                    groupKey = ConfigGroupKey.EQUIPMENT_TYPE,
                    label = label,
                    isBuiltIn = true,
                    isArchived = false,
                    orderIndex = index
                )
            }
        )
    }

    private fun ConfigOptionEntity.toDomain(): ConfigOption {
        return ConfigOption(
            id = id,
            groupKey = groupKey,
            label = label,
            isBuiltIn = isBuiltIn,
            isArchived = isArchived,
            orderIndex = orderIndex
        )
    }

}