package hedgehog.tech.fitnes.data.models

import androidx.room.*
import hedgehog.tech.fitnes.data.converters.DatabaseConverter

//сущность для хранения истории подтренировки

@Entity(foreignKeys = [
    ForeignKey(
        entity = SubWorkoutModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("sub_workout_id")),
])
@TypeConverters(DatabaseConverter::class)
data class SubWorkoutHistoryModel(
    @ColumnInfo(name = "sub_workout_id")
    var subWorkoutId: Int,
    var workoutTitle: String,
    var progress: MutableMap<Int,Boolean>, //ключи - порядковый номер упражнения, значение - выполнено/не выполнено
    @ColumnInfo(name= "workout_time")
    var workoutTime: Long,
    @ColumnInfo(name= "workout_date")
    var workoutDate: Long,
    var calories: Double
){
    @PrimaryKey(autoGenerate = true)
    var id = 0


}
