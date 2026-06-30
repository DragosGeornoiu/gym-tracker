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
import com.dragos.geornoiu.gymtracker.data.local.entities.ExerciseDefinitionEntity
import com.dragos.geornoiu.gymtracker.data.local.entities.ConfigOptionEntity

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY date DESC, updatedAt DESC")
    fun observeWorkouts(): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Query(
        """
        SELECT * FROM workout_entries 
        WHERE workoutId = :workoutId 
        AND parentEntryId IS NULL 
        ORDER BY orderIndex ASC
    """
    )
    fun observeRootWorkoutEntries(workoutId: Long): Flow<List<WorkoutEntryEntity>>

    @Query(
        """
        SELECT * FROM workout_entries 
        WHERE parentEntryId = :parentEntryId 
        ORDER BY orderIndex ASC
    """
    )
    fun observeChildWorkoutEntries(parentEntryId: Long): Flow<List<WorkoutEntryEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM workout_entries 
        WHERE workoutId = :workoutId 
        AND parentEntryId IS NULL
    """
    )
    suspend fun getRootWorkoutEntryCount(workoutId: Long): Int

    @Query(
        """
        SELECT COUNT(*) FROM workout_entries 
        WHERE parentEntryId = :parentEntryId
    """
    )
    suspend fun getChildWorkoutEntryCount(parentEntryId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutEntry(entry: WorkoutEntryEntity): Long

    @Query(
        """
        SELECT * FROM strength_sets 
        WHERE workoutEntryId = :workoutEntryId 
        ORDER BY orderIndex ASC
    """
    )
    fun observeStrengthSets(workoutEntryId: Long): Flow<List<StrengthSetEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM strength_sets 
        WHERE workoutEntryId = :workoutEntryId
    """
    )
    suspend fun getStrengthSetCount(workoutEntryId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrengthSet(set: StrengthSetEntity): Long

    @Query(
        """
        SELECT * FROM cardio_entries 
        WHERE workoutEntryId = :workoutEntryId 
        LIMIT 1
    """
    )
    fun observeCardioEntry(workoutEntryId: Long): Flow<CardioEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardioEntry(cardioEntry: CardioEntryEntity): Long

    @Delete
    suspend fun deleteStrengthSet(set: StrengthSetEntity)

    @Query("UPDATE strength_sets SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateStrengthSetOrder(id: Long, orderIndex: Int)

    @Update
    suspend fun updateStrengthSet(set: StrengthSetEntity)

    @Query(
        """
    SELECT * FROM exercise_definitions
    WHERE isArchived = 0
    ORDER BY name ASC
"""
    )
    fun observeExerciseDefinitions(): Flow<List<ExerciseDefinitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseDefinition(exerciseDefinition: ExerciseDefinitionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseDefinitions(exerciseDefinitions: List<ExerciseDefinitionEntity>)

    @Query("SELECT COUNT(*) FROM exercise_definitions")
    suspend fun getExerciseDefinitionCount(): Int

    @Query("""
    SELECT * FROM config_options
    WHERE groupKey = :groupKey
    AND isArchived = 0
    ORDER BY orderIndex ASC, label ASC
""")
    fun observeConfigOptions(groupKey: String): Flow<List<ConfigOptionEntity>>

    @Query("SELECT COUNT(*) FROM config_options WHERE groupKey = :groupKey")
    suspend fun getConfigOptionCountForGroup(groupKey: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigOption(option: ConfigOptionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigOptions(options: List<ConfigOptionEntity>)

    @Update
    suspend fun updateExerciseDefinition(exerciseDefinition: ExerciseDefinitionEntity)

    @Query("UPDATE exercise_definitions SET isArchived = 1 WHERE id = :id")
    suspend fun archiveExerciseDefinition(id: Long)

    @Query("SELECT COUNT(*) FROM workout_entries WHERE exerciseDefinitionId = :exerciseDefinitionId")
    suspend fun getWorkoutEntryCountForExerciseDefinition(exerciseDefinitionId: Long): Int

    @Query("DELETE FROM workout_entries WHERE id = :id")
    suspend fun deleteWorkoutEntryById(id: Long)

    @Update
    suspend fun updateWorkoutEntry(entry: WorkoutEntryEntity)

    @Update
    suspend fun updateConfigOption(option: ConfigOptionEntity)

    @Query("UPDATE config_options SET isArchived = 1 WHERE id = :id")
    suspend fun archiveConfigOption(id: Long)

    @Query("SELECT COUNT(*) FROM exercise_definitions WHERE equipmentTypeOptionId = :optionId AND isArchived = 0")
    suspend fun getExerciseDefinitionCountForEquipmentType(optionId: Long): Int

    @Query("""
    UPDATE workout_entries
    SET name = :name,
        type = :type,
        loadMode = :loadMode
    WHERE exerciseDefinitionId = :exerciseDefinitionId
""")
    suspend fun updateWorkoutEntriesForExerciseDefinition(
        exerciseDefinitionId: Long,
        name: String,
        type: String,
        loadMode: String
    )

    @Query("""
    UPDATE workouts
    SET title = :title,
        date = :date,
        notes = :notes,
        updatedAt = :updatedAt
    WHERE id = :id
""")
    suspend fun updateWorkoutMetadata(
        id: Long,
        title: String,
        date: String,
        notes: String,
        updatedAt: Long
    )

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: Long)


}