package com.dragos.geornoiu.gymtracker.domain

data class StrengthSet(
    val id: Long,
    val workoutEntryId: Long,
    val orderIndex: Int,
    val weightKg: Double?,
    val reps: Int?,
    val isWarmup: Boolean,
    val effortRating: EffortRating?,
    val note: String
)