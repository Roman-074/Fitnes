package hedgehog.tech.fitnes.app.utils

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.WorkoutModel

object Constants {
    //Хранит id стандартных фото для аватара пользователя
    val STANDART_PHOTOS = arrayListOf(
        R.drawable.reg_photo_standart_robot,
        R.drawable.reg_photo_standart_shark,
        R.drawable.reg_photo_standart_chicken,
        R.drawable.reg_photo_standart_monkey,
        R.drawable.reg_photo_standart_unicorn,
        R.drawable.reg_photo_standart_alien,
        R.drawable.reg_photo_standart_heart,
        R.drawable.reg_photo_standart_ghost,
        R.drawable.reg_photo_standart_cat,
    )
    //Код для

    //Ключи для данных в бэкстэках
    const val PROGRESS_DIALOG_BACK_KEY = "bac"
    const val REMINDER_DIALOG_BACK_KEY = "avc"
    const val CONFIRM_EXIT_EXERCISE_DIALOG_BACK_KEY = "avc"
                                  //константы для работы сервиса будильников
    const val WORKOUT_TITLE_KEY = "workoutTitleKey"
    const val WORKOUT_ID_KEY = "workoutIdKey"
    const val CHANNEL_ID = "notificationChannelId"
    const val NOTIFICATION_TO_ACTIVITY_REQUEST_CODE = 34
        //айдишник тренировки разогрева(такой же как а БД)
    const val WARM_UP_SUBWORKOUT_ID = 30

    //  фото группы мышц, используемых в упражнениях 7днеаного курса
    val SEVEN_DAYS_COURSE_SUBWORKOUTS_ICONS = mapOf(
        Pair(R.string.legs,R.drawable.fragment_seven_days_training_course_img_src_legs),
        Pair(R.string.arms_and_chest,R.drawable.fragment_seven_days_training_course_img_src_arms_and_chest),
        Pair(R.string.stretching,R.drawable.fragment_seven_days_training_course_img_src_stretching),
        Pair(R.string.press,R.drawable.fragment_seven_days_training_course_img_src_press),
        Pair(R.string.plank,R.drawable.fragment_seven_days_training_course_img_src_plank),
        Pair(R.string.shoulders_and_back,R.drawable.fragment_seven_days_training_course_img_src_shoulders),
    )


        //анимации всех упражнение на окне exerciseInfo
    val EXERCISE_ANIMATIONS_MAP = mapOf(
        Pair(
            Triple(WorkoutModel.TYPE_GYMNASTIC,SubWorkoutModel.ALL,"male"),
            listOf(
                R.raw.exercise_men_arm_stretches,
                R.raw.exercise_men_shoulder_warm_up,
                R.raw.exercise_men_head_rotation,
                R.raw.exercise_men_shoulder_rotation,
                R.raw.exercise_men_elbows_rotation,
                R.raw.exercise_men_straight_arms_rotation,
                R.raw.exercise_men_head_turns,
                R.raw.exercise_men_thoracic_twists,
                R.raw.exercise_men_body_lateral_displacements,
                R.raw.exercise_men_body_circular_displacements,
                R.raw.exercise_men_lateral_traction,
                R.raw.exercise_men_body_turns,
                R.raw.exercise_men_body_rotation,
                R.raw.exercise_men_body_bends,
            )
        ),
        Pair(
            Triple(WorkoutModel.TYPE_GYMNASTIC,SubWorkoutModel.ALL,"female"),
            listOf(
                R.raw.exercise_men_arm_stretches,
                R.raw.exercise_men_shoulder_warm_up,
            )
        )
    )

}