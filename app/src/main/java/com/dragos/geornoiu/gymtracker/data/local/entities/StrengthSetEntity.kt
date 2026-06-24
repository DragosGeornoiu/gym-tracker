package com.dragos.geornoiu.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "strength_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutEntryId")]
)
data class StrengthSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutEntryId: Long,
    val orderIndex: Int,
    val weightKg: Double?,
    val reps: Int?,
    val isWarmup: Boolean = false,
    val effortRating: String? = null,
    val note: String = ""
)