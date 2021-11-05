package hedgehog.tech.fitnes.ui.main.workouts.exerciseInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.*
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ExerciseInfoViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {


    val user = MutableLiveData<Resource<UserModel>>()
    val subWorkoutExercises = MutableLiveData<Resource<List<SubWorkoutExerciseModel>>>()
    val exercise = MutableLiveData<Resource<ExerciseModel>>()
    val isSuccessInsertSubWorkoutHistory = MutableLiveData<Resource<Unit>>()
    val subWorkout = MutableLiveData<Resource<SubWorkoutModel>>()
    val workout = MutableLiveData<Resource<WorkoutModel>>()
    val calories = MutableLiveData<Resource<Double>>()

    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    fun getAllExercisesBySubWorkoutId(id: Int) = viewModelScope.launch {
        subWorkoutExercises.postValue(databaseRepository.getAllExercisesBySubWorkoutId(id))
    }

    fun getExerciseById(id: Int) = viewModelScope.launch {
        exercise.postValue(databaseRepository.getExerciseById(id))
    }

        //вставляем в БД запись о пройденной тренировке
    fun insertSubWorkoutHistoryAndWorkoutHistory(
        item: SubWorkoutHistoryModel,
        workoutModelId: Int,
        isChangeProgress: Boolean,
    ) = viewModelScope.launch {
            //сначала берем историю тренировки
        val historyModelResource = databaseRepository.getWorkoutHistoryByWorkout(workoutModelId)
        if (historyModelResource is Resource.Success) {
            historyModelResource.data?.let {
                // если нужно менять запись, меняем(не нужно в случае если мы перешли на
                // эту подтренировку сами, а не по программе)
                if (isChangeProgress) it.subWorkoutProgress++
                if (databaseRepository.updateWorkoutHistory(it) is Resource.Success) {
                    //если успешно обновили историю тренировки, обновляем историю подтренировки
                    isSuccessInsertSubWorkoutHistory.postValue(
                        databaseRepository.insertSubWorkoutHistory(
                            item
                        )
                    )
                    return@launch
                }
            }
        }
        isSuccessInsertSubWorkoutHistory.postValue(Resource.Error())
    }

    fun getSubworkoutById(id: Int) = viewModelScope.launch {
        subWorkout.postValue(databaseRepository.getSubWorkoutById(id))
    }

    fun getWorkoutById(id: Int) = viewModelScope.launch {
        workout.postValue(databaseRepository.getWorkoutById(id))
    }

    fun updateSubWorkout(subWorkoutModel: SubWorkoutModel) = viewModelScope.launch {
        Timber.d("$subWorkoutModel")
        databaseRepository.updateSubWorkout(subWorkoutModel)
    }
    //подсчитываем калории
    fun calculateCalories(
        historyModelSub: SubWorkoutHistoryModel,
        exerciseTimes: MutableMap<Int, Long>,
    ) = viewModelScope.launch {
        //получаем все упражнения данной тренировки
        val exercisesResource =
            databaseRepository.getAllExercisesBySubWorkoutId(historyModelSub.subWorkoutId)
        val userResource = databaseRepository.getUser()
        if ((exercisesResource is Resource.Success) and (exercisesResource.data != null)) {
            val exercises = exercisesResource.data!!
            var allCalories = 0.0
                //проходимся по истории тренировки, чтобы узнать какие упражнения выполнены
            historyModelSub.progress.forEach {
                //если упражнение выполнено, то заходим внутрь условия
                if (it.value) {
                    exerciseTimes[it.key]?.let { time ->
                        val exerciseResource =
                            getExerciseDataById(exercises[it.key - 1].exerciseId)
                        if (
                            (exerciseResource is Resource.Success) and
                            (exerciseResource.data != null) and
                            (userResource is Resource.Success) and
                            (userResource.data != null)
                        ) {
                            //Подсчитываем калории на основе веса и времени упражнения
                            val exerciseCalories =
                                exerciseResource.data!!.kcalBurnedOnOneKgPerMinute /
                                        60_000 *
                                        userResource.data!!.currWeight!! * time
                            allCalories += exerciseCalories
                        } else {
                            calories.postValue(Resource.Error())
                            return@launch
                        }
                    }
                }
            }
            calories.postValue(Resource.Success(allCalories))


        } else {
            calories.postValue(Resource.Error())
            return@launch
        }
    }

    private suspend fun getExerciseDataById(exerciseId: Int): Resource<ExerciseModel> {
        Timber.d("exerciseId $exerciseId")
        return databaseRepository.getExerciseById(exerciseId)
    }


}