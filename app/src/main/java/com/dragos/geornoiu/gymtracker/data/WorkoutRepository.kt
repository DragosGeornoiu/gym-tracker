package com.dragos.geornoiu.gymtracker.data

import com.dragos.geornoiu.gymtracker.data.local.WorkoutDao
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntryEntity
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntry
import com.dragos.geornoiu.gymtracker.domain.WorkoutEntryType
import com.dragos.geornoiu.gymtracker.domain.WorkoutSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

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
                title = "Workout",
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
            note = note
        )
    }
}