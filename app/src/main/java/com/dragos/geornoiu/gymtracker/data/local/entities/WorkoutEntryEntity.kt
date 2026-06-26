package com.dragos.geornoiu.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_entries",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutId"),
        Index("parentEntryId")
    ]
)
data class WorkoutEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutId: Long,
    val parentEntryId: Long? = null,
    val type: String,
    val name: String,
    val orderIndex: Int,
    val isWarmup: Boolean = false,
    val loadMode: String = "TOTAL",
    val note: String = ""
)