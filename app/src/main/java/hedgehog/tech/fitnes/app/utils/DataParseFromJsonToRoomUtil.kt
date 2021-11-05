package hedgehog.tech.fitnes.app.utils

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hedgehog.tech.fitnes.data.dao.ExerciseDao
import hedgehog.tech.fitnes.data.dao.SubWorkoutDao
import hedgehog.tech.fitnes.data.dao.SubWorkoutExerciseDao
import hedgehog.tech.fitnes.data.dao.WorkoutDao
import hedgehog.tech.fitnes.data.models.ExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

//класс для парсинга данных и json в записи в БД
object DataParseFromJsonToRoomUtil {
    private val gson = Gson()
    suspend fun parseWorkouts(fileName:String, workoutDao: WorkoutDao,context:Context){
        Timber.d("start parse")

        val assetManager: AssetManager = context.assets
        try {
            //Забираем нужный файл из ассетов, и превращаем его в список сущносей
            val inputStream: InputStream = assetManager.open(fileName)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val workouts: List<WorkoutModel> = gson.fromJson(
                inputString, object : TypeToken<List<WorkoutModel>>() {}.type
            )
            //заносим сущности в БД с помощью Data access object
            workouts.forEach {
                workoutDao.insert(it)
            }
            Timber.d("Success parse $workouts")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun parseExercises(fileName:String, exerciseDao: ExerciseDao,context:Context){
        Timber.d("start parse")
        val assetManager: AssetManager = context.assets
        try {
            //Забираем нужный файл из ассетов, и превращаем его в список сущносей
            val inputStream: InputStream = assetManager.open(fileName)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val exercises: List<ExerciseModel> = gson.fromJson(
                inputString, object : TypeToken<List<ExerciseModel>>() {}.type
            )
            //заносим сущности в БД с помощью Data access object
            exercises.forEach {
                exerciseDao.insert(it)
            }
            Timber.d("Success parse $exercises")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun parseSubWorkouts(fileName:String, subWorkoutDao: SubWorkoutDao,context:Context){
        Timber.d("start parse")
        val assetManager: AssetManager = context.assets
        try {
            //Забираем нужный файл из ассетов, и превращаем его в список сущносей
            val inputStream: InputStream = assetManager.open(fileName)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val subWorkouts: List<SubWorkoutModel> = gson.fromJson(
                inputString, object : TypeToken<List<SubWorkoutModel>>() {}.type
            )
            //заносим сущности в БД с помощью Data access object
            subWorkouts.forEach {
                subWorkoutDao.insert(it)
            }
            Timber.d("Success parse $subWorkouts")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun parseSubWorkoutExercises(fileName:String, subWorkoutExerciseDao: SubWorkoutExerciseDao,context:Context){
        Timber.d("start parse")
        val assetManager: AssetManager = context.assets
        try {
            //Забираем нужный файл из ассетов, и превращаем его в список сущносей
            val inputStream: InputStream = assetManager.open(fileName)
            val inputString = inputStream.bufferedReader().use { it.readText() }
            val subWorkoutExercises: List<SubWorkoutExerciseModel> = gson.fromJson(
                inputString, object : TypeToken<List<SubWorkoutExerciseModel>>() {}.type
            )
            //заносим сущности в БД с помощью Data access object
            subWorkoutExercises.forEach {
                subWorkoutExerciseDao.insert(it)
                Timber.d("Insert: $it")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}