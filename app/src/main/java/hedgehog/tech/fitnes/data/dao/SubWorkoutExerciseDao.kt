package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel


@Dao
interface SubWorkoutExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SubWorkoutExerciseModel)

    @Update
    suspend fun update(item: SubWorkoutExerciseModel)

    @Query("SELECT * FROM SubWorkoutExerciseModel")
    suspend fun getSubWorkoutExercises() : List<SubWorkoutExerciseModel>

    @Query("SELECT * FROM subworkoutexercisemodel where sub_workout_id=:subWorkoutId order by queue")
    suspend fun getAllExercisesBySubWorkoutId(subWorkoutId: Int): List<SubWorkoutExerciseModel>
}