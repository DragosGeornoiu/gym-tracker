package com.dragos.geornoiu.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_definitions")
data class ExerciseDefinitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val defaultType: String,
    val defaultLoadMode: String,
    val isBuiltIn: Boolean = false,
    val isArchived: Boolean = false
)