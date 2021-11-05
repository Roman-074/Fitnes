package hedgehog.tech.fitnes.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//сущность для связи упражнений с подтренировками
@Entity(
    foreignKeys = [
        ForeignKey(
        entity = SubWorkoutModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("sub_workout_id")),
        ForeignKey(
            entity = ExerciseModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exercise_id")),
    ]
)
data class SubWorkoutExerciseModel(
    var title: String,
    @ColumnInfo(name = "sub_workout_id")
    var subWorkoutId: Int,
    @ColumnInfo(name = "exercise_id")
    var exerciseId: Int,
    @ColumnInfo(name = "repetitions_count")
    var repetitionsCount: Int?, //количество повторений
    @ColumnInfo(name = "approaches_count")
    var approachesCount: Int?,//количество подходов
    @ColumnInfo(name = "time")
    var time: Long?, // дата выполнения
    @ColumnInfo(name = "queue")
    var queue: Int? // порядковый номер выполнения в подтренировке
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
