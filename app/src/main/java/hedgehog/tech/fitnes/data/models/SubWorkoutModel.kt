package hedgehog.tech.fitnes.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
// сущность для хранения подтренировки
data class SubWorkoutModel(
    @ColumnInfo(name = "workout_id")
    var workoutId: Int,
    var title: String?,
    var time: String,
    var exerciseCount: Int?,
    var completedCount: Int?,
    var hardType: Int, // тип навыков пользователя для этой подтренировки
    var type: Int, //тип подтренировки
    var difficulty: Int = 0,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    companion object {
        // типы навыков
        const val WEAK = 0
        const val ADVANCED = 1
        const val MASTER = 2
        const val ALL = 4

        //типы подтренировок 7дневного курса
        const val TYPE_7_DAYS_TRAINING_COURSE_1 = 4
        const val TYPE_7_DAYS_TRAINING_COURSE_2 = 5
        const val TYPE_7_DAYS_TRAINING_COURSE_3 = 6
        const val TYPE_7_DAYS_TRAINING_COURSE_4 = 7
        const val TYPE_7_DAYS_TRAINING_COURSE_5 = 8
        const val TYPE_7_DAYS_TRAINING_COURSE_6 = 9
        const val TYPE_7_DAYS_TRAINING_COURSE_7 = 10

        //типы подтренировок курса тренировки различных групп мыщц
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_ARMS_AND_CHEST = 11
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_PRESS = 12
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_STRETCHING = 13
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_LEGS = 14
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_PLANK = 15
        const val TYPE_SPECIFIC_MUSCLE_GROUPS_SHOULDERS_AND_BACK = 16
        const val TYPE_NOTHING = 17
    }
}
