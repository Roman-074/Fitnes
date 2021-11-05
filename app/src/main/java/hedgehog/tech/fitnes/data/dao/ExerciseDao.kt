package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.ExerciseModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel


@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ExerciseModel)

    @Query("SELECT * FROM exercisemodel")
    suspend fun getExercises() : List<ExerciseModel>

    @Query("SELECT * FROM exercisemodel where id=:id")
    suspend fun getExerciseById(id: Int) : ExerciseModel
}