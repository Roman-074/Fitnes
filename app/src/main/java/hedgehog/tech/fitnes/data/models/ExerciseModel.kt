package hedgehog.tech.fitnes.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

//Cушность упражнений
@Entity
data class ExerciseModel(
    val title: String,
    val description: String,
    var kcalBurnedOnOneKgPerMinute: Double,
    var maleAnimPath: String,
    var femaleAnimPath: String
)  {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
