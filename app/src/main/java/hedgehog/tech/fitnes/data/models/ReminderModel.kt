package hedgehog.tech.fitnes.data.models

import androidx.room.*
import hedgehog.tech.fitnes.app.utils.DaysOfWeek
import hedgehog.tech.fitnes.data.converters.DatabaseConverter

//Cущность хранящая напоминалки
@Entity(foreignKeys = [
    ForeignKey(
        entity = WorkoutModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("workout_id")),
])
@TypeConverters(DatabaseConverter::class)
data class ReminderModel(
    @ColumnInfo(name = "workout_id")
    var workoutId: Int,
    val time: Long,
    val repetitions: List<DaysOfWeek>,
    var isEnable: Boolean,
)  {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var alarmServiceIdList = mutableListOf<Int>()



}
