package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.data.models.UserModel


@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ReminderModel)

    @Query("SELECT * FROM remindermodel")
    suspend fun getReminders() : List<ReminderModel>

    @Query("SELECT * FROM remindermodel where id = :id")
    suspend fun getReminder(id: Int) : ReminderModel

    @Query("DELETE FROM remindermodel where id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM remindermodel where workout_id = :id")
    suspend fun getRemindersByWorkoutId(id: Int) : List<ReminderModel>
}