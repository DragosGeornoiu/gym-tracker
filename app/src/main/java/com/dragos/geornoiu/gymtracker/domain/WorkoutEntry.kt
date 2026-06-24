package com.dragos.geornoiu.gymtracker.domain

data class WorkoutEntry(
    val id: Long,
    val workoutId: Long,
    val parentEntryId: Long?,
    val type: WorkoutEntryType,
    val name: String,
    val orderIndex: Int,
    val isWarmup: Boolean,
    val note: String
)