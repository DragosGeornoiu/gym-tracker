package com.dragos.geornoiu.gymtracker.domain

data class ExerciseDefinition(
    val id: Long,
    val name: String,
    val defaultType: WorkoutEntryType,
    val defaultLoadMode: LoadMode,
    val isBuiltIn: Boolean,
    val isArchived: Boolean
)