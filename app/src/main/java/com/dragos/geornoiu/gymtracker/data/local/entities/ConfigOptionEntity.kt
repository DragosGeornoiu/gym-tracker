package com.dragos.geornoiu.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "config_options",
    indices = [
        Index(value = ["groupKey", "label"], unique = true)
    ]
)
data class ConfigOptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupKey: String,
    val label: String,
    val isBuiltIn: Boolean = false,
    val isArchived: Boolean = false,
    val orderIndex: Int = 0
)