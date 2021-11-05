package hedgehog.tech.fitnes.data.models

import androidx.room.*
import hedgehog.tech.fitnes.data.converters.DatabaseConverter

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("workout_id")
        ),
    ]
)
@TypeConverters(DatabaseConverter::class)
//Модель для хранения прогресса программы тренировок
data class WorkoutHistoryModel(
    @ColumnInfo(name = "workout_id")
    var workoutId: Int,
    var subWorkoutProgress: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}
