package com.dragos.geornoiu.gymtracker.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cardio_entries",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workoutEntryId"], unique = true)]
)
data class CardioEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workoutEntryId: Long,

    // What you planned to do
    val targetType: String,
    val targetValue: Double,

    // What actually happened
    val durationSeconds: Int? = null,
    val distanceMeters: Int? = null,
    val calories: Int? = null,

    val effortRating: String? = null,
    val note: String = ""
)