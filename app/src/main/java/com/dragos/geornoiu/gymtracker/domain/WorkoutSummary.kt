package com.dragos.geornoiu.gymtracker.domain

data class WorkoutSummary(
    val id: Long,
    val date: String,
    val title: String,
    val exerciseCount: Int,
    val summary: String
)