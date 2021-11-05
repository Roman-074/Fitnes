package hedgehog.tech.fitnes.data.database

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.RoomDatabase
import com.google.gson.Gson
import hedgehog.tech.fitnes.app.BaseApp
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.DataParseFromJsonToRoomUtil
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.dao.*
import hedgehog.tech.fitnes.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


@Database(
    entities = [
        UserModel::class,
        WorkoutModel::class,
        ExerciseModel::class,
        SubWorkoutExerciseModel::class,
        SubWorkoutHistoryModel::class,
        ReminderModel::class,
        SubWorkoutModel::class,
        WorkoutHistoryModel::class,
               ],
    version = 24
)
//Класс БД
abstract class AppDatabase() : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutExerciseDao(): SubWorkoutExerciseDao
    abstract fun subWorkoutHistoryDao(): SubWorkoutHistoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun subWorkoutDao(): SubWorkoutDao
    abstract fun workoutHistoryDao(): WorkoutHistoryDao


    companion object {
        //Переменная, показывающася создана ли БД
        var isDatabaseCreate = false
        //Функция для заноса данных в БД при ее создании
        private suspend fun populateDatabase(
            workoutDao: WorkoutDao,
            exerciseDao: ExerciseDao,
            subWorkoutExerciseDao: SubWorkoutExerciseDao,
            subWorkoutHistoryDao: SubWorkoutHistoryDao,
            reminderDao: ReminderDao,
            subWorkoutDao: SubWorkoutDao,
            workoutHistoryDao: WorkoutHistoryDao,
        ) {
            //Основные тренировки(в меню workouts)
            DataParseFromJsonToRoomUtil.parseWorkouts("enWorkouts.json",workoutDao,BaseApp.getInstance())
            //Упражнения
            DataParseFromJsonToRoomUtil.parseExercises("enExercises.json",exerciseDao,BaseApp.getInstance())
            //Подтренировки
            DataParseFromJsonToRoomUtil.parseSubWorkouts("enSubWorkouts.json",subWorkoutDao,BaseApp.getInstance())
            //Упражнения подтренировок
            DataParseFromJsonToRoomUtil.parseSubWorkoutExercises("enSubWorkoutExercises.json",subWorkoutExerciseDao,BaseApp.getInstance())
            isDatabaseCreate = true
        }

        suspend fun onCreate(database: AppDatabase) {
            populateDatabase(
                database.workoutDao(),
                database.exerciseDao(),
                database.workoutExerciseDao(),
                database.subWorkoutHistoryDao(),
                database.reminderDao(),
                database.subWorkoutDao(),
                database.workoutHistoryDao(),
            )
        }
    }
}