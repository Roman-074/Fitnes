package hedgehog.tech.fitnes.repositories

import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.dao.*
import hedgehog.tech.fitnes.data.database.AppDatabase
import hedgehog.tech.fitnes.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val userDao: UserDao,
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val subWorkoutExerciseDao: SubWorkoutExerciseDao,
    private val subWorkoutHistoryDao: SubWorkoutHistoryDao,
    private val workoutHistoryDao: WorkoutHistoryDao,
    private val reminderDao: ReminderDao,
    private val subWorkoutDao: SubWorkoutDao,
) {

    private inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
        return try {
            action()
        } catch (e: Exception) {
            Timber.d(e)
            Resource.Error(e)
        }
    }

    fun isDatabaseCreate() = AppDatabase.isDatabaseCreate




    suspend fun insertUser(item: UserModel) = withContext(Dispatchers.IO) {
        safeCall {
            userDao.insert(item)
            Timber.d("Insert user: %s", userDao.getUser().toString())
            Resource.Success<Unit>()
        }
    }

    suspend fun getUser() = withContext(Dispatchers.IO) {
        safeCall {
            if (userDao.getUser() == null) {
                userDao.insert(
                    UserModel(
                        1,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        true,
                        null,
                        null
                    )
                )
            }
            Timber.d("Get user: %s", userDao.getUser().toString())
            Resource.Success(userDao.getUser())
        }
    }

    suspend fun getWorkoutHistoryByWorkout(workoutModelId: Int) = withContext(Dispatchers.IO) {
        safeCall {
            if (workoutHistoryDao.getWorkoutHistoryByWorkoutId(workoutModelId) == null) {
                val workoutHistoryModel = WorkoutHistoryModel(workoutModelId)
                workoutHistoryDao.insert(workoutHistoryModel)
            }
            Resource.Success(workoutHistoryDao.getWorkoutHistoryByWorkoutId(workoutModelId))
        }
    }

    suspend fun getWorkouts() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(workoutDao.getWorkouts())
        }
    }

    suspend fun getWorkoutById(id: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(workoutDao.getWorkoutById(id))
        }
    }

    suspend fun getSubWorkoutById(id: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(subWorkoutDao.getSubWorkoutById(id))
        }
    }

    suspend fun getSubWorkoutsByWorkoutId(id: Int, type: Int) = withContext(Dispatchers.IO) {
        safeCall {
            val result = subWorkoutDao.getSubWorkoutByWorkoutId(id, type)
            Timber.d("Subworkouts: %s", result.toString())
            Resource.Success(result)
        }
    }


    suspend fun getAllExercisesBySubWorkoutId(id: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(subWorkoutExerciseDao.getAllExercisesBySubWorkoutId(id))
        }
    }

    suspend fun getExerciseById(id: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(exerciseDao.getExerciseById(id))
        }
    }

    suspend fun insertSubWorkoutHistory(item: SubWorkoutHistoryModel) =
        withContext(Dispatchers.IO) {
            safeCall {

                Timber.d("Insert workout history: %s", item)
                Resource.Success(subWorkoutHistoryDao.insert(item))
            }
        }

    suspend fun getLastSubWorkoutHistory() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate().first()
            )
        }
    }

    suspend fun getAllSubWorkoutsHistorySortedByDate() = withContext(Dispatchers.IO) {
        safeCall {
            val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
            val mainList = mutableListOf<List<SubWorkoutHistoryModel>>()
            val yearsSet = mutableSetOf<Int>()
            history.forEach {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.workoutDate
                yearsSet.add(calendar.get(Calendar.YEAR))
            }

            yearsSet.forEach { year ->
                val yearList = history.filter {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.workoutDate
                    calendar.get(Calendar.YEAR) == year
                }
                val monthSet = mutableSetOf<Int>()
                yearList.forEach {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.workoutDate
                    monthSet.add(calendar.get(Calendar.MONTH))
                }
                monthSet.forEach { month ->
                    val monthList = yearList.filter {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.workoutDate
                        calendar.get(Calendar.MONTH) == month
                    }
                    mainList.add(monthList)
                }
            }
            Resource.Success(mainList)
        }

    }

    suspend fun getCompletedExerciseStatistics() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(
                ExerciseStatisticModel(
                    getWorkoutCompletedExercises(),
                    getWeekCompletedExercises(),
                    getMonthCompletedExercises(),
                    getYearCompletedExercises(),
                    getAllCompletedExercises()
                )
            )
        }
    }


    suspend fun getAllCompletedExercises() = withContext(Dispatchers.IO) {
        val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
        var countForAll = 0
        history.forEach {
            it.progress.forEach { row ->
                if (row.value) {
                    countForAll++
                }
            }
        }
        Timber.d("Count exercises: $countForAll")
        countForAll
    }

    private suspend fun getYearCompletedExercises() = withContext(Dispatchers.IO) {
        val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
        var count = 0
        history.forEach {
            val date = Calendar.getInstance()
            val currDate = Calendar.getInstance()
            date.timeInMillis = it.workoutDate
            if (date.get(Calendar.YEAR) == currDate.get(Calendar.YEAR)) {
                it.progress.forEach { row ->
                    if (row.value) {
                        count++
                    }
                }
            }
        }
        return@withContext count
    }

    private suspend fun getMonthCompletedExercises() = withContext(Dispatchers.IO) {
        val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
        var count = 0
        history.forEach {
            val date = Calendar.getInstance()
            val currDate = Calendar.getInstance()
            date.timeInMillis = it.workoutDate
            if (
                (date.get(Calendar.YEAR) == currDate.get(Calendar.YEAR)) and
                (date.get(Calendar.MONTH) == currDate.get(Calendar.MONTH))
            ) {
                it.progress.forEach { row ->
                    if (row.value) {
                        count++
                    }
                }
            }
        }
        count
    }

    private suspend fun getWeekCompletedExercises() = withContext(Dispatchers.IO) {
        val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
        var count = 0
        history.forEach {
            val date = Calendar.getInstance()
            val currDate = Calendar.getInstance()
            date.timeInMillis = it.workoutDate
            if (
                (date.get(Calendar.YEAR) == currDate.get(Calendar.YEAR)) and
                (date.get(Calendar.WEEK_OF_YEAR) == currDate.get(Calendar.WEEK_OF_YEAR))
            ) {
                it.progress.forEach { row ->
                    if (row.value) {
                        count++
                    }
                }
            }
        }
        count
    }

    suspend fun getSubWorkoutsHistorySortedByReverseDate() = withContext(Dispatchers.IO){
        safeCall { Resource.Success(subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()) }
    }



    private suspend fun getWorkoutCompletedExercises() = withContext(Dispatchers.IO) {
        val history = subWorkoutHistoryDao.getSubWorkoutsHistorySortedByReverseDate()
        Timber.d("Workout Completed Exercises: $history")
        var count = 0
        history.firstOrNull()?.let {
            it.progress.forEach { row ->
                if (row.value) {
                    count++
                }
            }
        }
        count
    }

    suspend fun getReminders() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(reminderDao.getReminders())
        }
    }

    suspend fun getRemindersByWorkoutId(workoutId: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(reminderDao.getRemindersByWorkoutId(workoutId))
        }
    }

    suspend fun getReminderById(reminderId: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(reminderDao.getReminder(reminderId))
        }
    }

    suspend fun insertReminder(item: ReminderModel) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(reminderDao.insert(item))
        }
    }

    suspend fun deleteReminder(reminderId: Int) = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(reminderDao.delete(reminderId))
        }
    }

    suspend fun changeEnableStatusOfReminderById(reminderId: Int, isEnable: Boolean) =
        withContext(Dispatchers.IO) {
            safeCall {
                val reminder = reminderDao.getReminder(reminderId)
                reminder.isEnable = isEnable
                insertReminder(reminder)
            }
        }

    suspend fun updateWorkoutHistory(workoutHistoryModel: WorkoutHistoryModel) =
        withContext(Dispatchers.IO) {
            safeCall {
                Resource.Success(workoutHistoryDao.update(workoutHistoryModel))
            }
        }

    suspend fun updateSubWorkout(subWorkoutModel: SubWorkoutModel) =
        withContext(Dispatchers.IO) {
            safeCall {
                Resource.Success(subWorkoutDao.update(subWorkoutModel))
            }
        }

    suspend fun updateSubWorkoutExercise(subWorkoutExerciseModel: SubWorkoutExerciseModel) =
        withContext(Dispatchers.IO) {
            safeCall {
                Resource.Success(subWorkoutExerciseDao.update(subWorkoutExerciseModel))
            }
        }


}