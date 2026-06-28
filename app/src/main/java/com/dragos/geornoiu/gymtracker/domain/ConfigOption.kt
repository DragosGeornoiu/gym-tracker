package com.dragos.geornoiu.gymtracker.domain

data class ConfigOption(
    val id: Long,
    val groupKey: String,
    val label: String,
    val isBuiltIn: Boolean,
    val isArchived: Boolean,
    val orderIndex: Int
)