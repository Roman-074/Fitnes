package hedgehog.tech.fitnes.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Cушность статистики выполненых упражнений
@Parcelize
data class ExerciseStatisticModel(
    val countForLastWorkout: Int,
    val countForLastWeek: Int,
    val countForLastMonth: Int,
    val countForLastYear: Int,
    val countForAll: Int,
): Parcelable