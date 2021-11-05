package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.data.models.WorkoutHistoryModel


@Dao
interface WorkoutHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WorkoutHistoryModel)

    @Query("SELECT * FROM workouthistorymodel where workout_id=:workoutId")
    suspend fun getWorkoutHistoryByWorkoutId(workoutId: Int): WorkoutHistoryModel

    @Update
    suspend fun update(item: WorkoutHistoryModel)
}