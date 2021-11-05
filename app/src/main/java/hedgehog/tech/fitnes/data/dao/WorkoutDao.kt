package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.WorkoutModel


@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutModel)

    @Query("SELECT * FROM workoutmodel")
    suspend fun getWorkouts() : List<WorkoutModel>

    @Query("SELECT * FROM workoutmodel where id=:id")
    suspend fun getWorkoutById(id: Int) : WorkoutModel

}