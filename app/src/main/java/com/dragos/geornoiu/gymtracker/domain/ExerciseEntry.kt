package com.dragos.geornoiu.gymtracker.domain

data class ExerciseEntry(
    val id: Long,
    val workoutId: Long,
    val name: String,
    val entryTypeId: String,
    val orderIndex: Int,
    val notes: String
)