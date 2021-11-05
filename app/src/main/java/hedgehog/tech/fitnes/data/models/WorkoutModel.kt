package hedgehog.tech.fitnes.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
// Сущность для тренировки
data class WorkoutModel(
    var title: String,
    var time: String,
    var type: Int // Тип тренировки
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    companion object{
        //все типы тренировок
        const val TYPE_GYMNASTIC = 0
        const val TYPE_7_DAYS_TRAINING_COURSE = 1
        const val TYPE_SPECIFIC_MUSCLE_GROUPS = 2
        const val TYPE_HIGH_BAR = 3
        const val TYPE_POWER_TRAINING = 4
        const val TYPE_WARM_UP = 4
    }
}
