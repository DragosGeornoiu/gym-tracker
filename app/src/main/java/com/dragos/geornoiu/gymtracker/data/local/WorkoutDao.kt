package com.dragos.geornoiu.gymtracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dragos.geornoiu.gymtracker.data.local.entities.CardioEntryEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.StrengthSetEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.WorkoutEntryEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
import androidx.room.Update

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY date DESC, updatedAt DESC")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Query("""
        SELECT * FROM workout_entries 
        WHERE workoutId = :workoutId 
        AND parentEntryId IS NULL 
        ORDER BY orderIndex ASC
    """)
    fun observeRootWorkoutEntries(workoutId: Long): Flow<List<WorkoutEntryEntity>>

    @Query("""
        SELECT * FROM workout_entries 
        WHERE parentEntryId = :parentEntryId 
        ORDER BY orderIndex ASC
    """)
    fun observeChildWorkoutEntries(parentEntryId: Long): Flow<List<WorkoutEntryEntity>>

    @Query("""
        SELECT COUNT(*) FROM workout_entries 
        WHERE workoutId = :workoutId 
        AND parentEntryId IS NULL
    """)
    suspend fun getRootWorkoutEntryCount(workoutId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM workout_entries 
        WHERE parentEntryId = :parentEntryId
    """)
    suspend fun getChildWorkoutEntryCount(parentEntryId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutEntry(entry: WorkoutEntryEntity): Long

    @Query("""
        SELECT * FROM strength_sets 
        WHERE workoutEntryId = :workoutEntryId 
        ORDER BY orderIndex ASC
    """)
    fun observeStrengthSets(workoutEntryId: Long): Flow<List<StrengthSetEntity>>

    @Query("""
        SELECT COUNT(*) FROM strength_sets 
        WHERE workoutEntryId = :workoutEntryId
    """)
    suspend fun getStrengthSetCount(workoutEntryId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrengthSet(set: StrengthSetEntity): Long

    @Query("""
        SELECT * FROM cardio_entries 
        WHERE workoutEntryId = :workoutEntryId 
        LIMIT 1
    """)
    fun observeCardioEntry(workoutEntryId: Long): Flow<CardioEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardioEntry(cardioEntry: CardioEntryEntity): Long

    @Delete
    suspend fun deleteStrengthSet(set: StrengthSetEntity)

    @Query("UPDATE strength_sets SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateStrengthSetOrder(id: Long, orderIndex: Int)

    @Update
    suspend fun updateStrengthSet(set: StrengthSetEntity)
}