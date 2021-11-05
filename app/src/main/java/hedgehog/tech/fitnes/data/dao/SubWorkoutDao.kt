package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.SubWorkoutModel


@Dao
interface SubWorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SubWorkoutModel)

    @Query("SELECT * FROM subworkoutmodel")
    suspend fun getSubWorkouts(): List<SubWorkoutModel>

    @Query("SELECT * FROM subworkoutmodel where id=:id")
    suspend fun getSubWorkoutById(id: Int): SubWorkoutModel

    @Query("SELECT * FROM subworkoutmodel where workout_id=:id and hardType=:hardType order by id")
    suspend fun getSubWorkoutByWorkoutId(id: Int, hardType: Int): List<SubWorkoutModel>


    @Update
    fun update(subWorkoutModel: SubWorkoutModel)

}