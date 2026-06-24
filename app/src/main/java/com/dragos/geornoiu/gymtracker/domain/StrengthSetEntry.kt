package com.dragos.geornoiu.gymtracker.domain

data class StrengthSetEntry(
    val id: Long,
    val exerciseEntryId: Long,
    val orderIndex: Int,
    val weightKg: Double,
    val reps: Int,
    val isWarmup: Boolean,
    val note: String
)