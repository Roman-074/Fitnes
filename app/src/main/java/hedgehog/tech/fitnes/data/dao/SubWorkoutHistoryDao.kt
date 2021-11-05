package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel


@Dao
interface SubWorkoutHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SubWorkoutHistoryModel)

    @Query("SELECT * FROM subworkouthistorymodel order by workout_date desc")
    suspend fun getSubWorkoutsHistorySortedByReverseDate(): List<SubWorkoutHistoryModel>

    @Query("SELECT * FROM subworkouthistorymodel where sub_workout_id=:subWorkoutId order by workout_date desc")
    suspend fun getSubWorkoutHistoryBySubWorkoutId(subWorkoutId: Int): List<SubWorkoutHistoryModel>

    @Query("SELECT * FROM subworkouthistorymodel where id=:id")
    suspend fun getSubWorkoutHistoryById(id: Int): SubWorkoutHistoryModel
}