package com.dragos.geornoiu.gymtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dragos.geornoiu.gymtracker.data.local.entities.CardioEntryEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.StrengthSetEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntryEntity

@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutEntryEntity::class,
        StrengthSetEntity::class,
        CardioEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GymTrackerDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: GymTrackerDatabase? = null

        fun getDatabase(context: Context): GymTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymTrackerDatabase::class.java,
                    "gym_tracker_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}