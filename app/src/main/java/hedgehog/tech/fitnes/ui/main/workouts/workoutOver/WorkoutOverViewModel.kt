package hedgehog.tech.fitnes.ui.main.workouts.workoutOver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.*
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.RuntimeException
import javax.inject.Inject


@HiltViewModel
class WorkoutOverViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val user = MutableLiveData<Resource<UserModel>>()
    val lastWorkoutHistory = MutableLiveData<Resource<SubWorkoutHistoryModel>>()
    val subworkout = MutableLiveData<Resource<SubWorkoutModel>>()
    val isSuccessUpdateDifficulty = MutableLiveData<Resource<Unit>>()


    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    fun getLastWorkoutHistory() = viewModelScope.launch {
        lastWorkoutHistory.postValue(databaseRepository.getLastSubWorkoutHistory())
    }

    fun getSubworkoutById(subworkoutId: Int) = viewModelScope.launch {
        subworkout.postValue(databaseRepository.getSubWorkoutById(subworkoutId))
    }
    //Если пользователь 3 раза нажал что, ему плохо/хорошо после тренировки, меняем количество упражнений
    fun changeDifficulty(subworkout: SubWorkoutModel, newDifficulty: Int) = viewModelScope.launch {
        when {
            newDifficulty >= 3 -> {
                isSuccessUpdateDifficulty.postValue(upDifficulty(subworkout))
                return@launch

            }
            newDifficulty <= -3 -> {
                isSuccessUpdateDifficulty.postValue(downDifficulty(subworkout))
                return@launch
            }
            else -> {
                subworkout.difficulty = newDifficulty
                isSuccessUpdateDifficulty.postValue(databaseRepository.updateSubWorkout(subworkout))
            }
        }
    }

    private suspend fun downDifficulty(subworkout: SubWorkoutModel): Resource<Unit> {
        //получем упражнения подтренировки
       val response = databaseRepository.getAllExercisesBySubWorkoutId(subworkout.id)
        if ((response is Resource.Success) && (response.data != null) ){
            val exercises = response.data.toMutableList()
            // проходимся по ним, если он про времени, меняем время, аналогично по повторениям
            exercises.forEach {
                if ((it.time != null)) {
                    if(it.time!! > 10000){
                        it.time = it.time!!.toLong() - (10 * 1000).toLong()
                    }
                } else if (it.repetitionsCount!! > 2){
                    it.repetitionsCount = it.repetitionsCount!! - 2
                }
                databaseRepository.updateSubWorkoutExercise(it)
            }
            subworkout.difficulty = 0
            return databaseRepository.updateSubWorkout(subworkout)
        }
        return Resource.Error(RuntimeException(),"Error with down difficulty")
    }
    //аналогично downDifficulty
    private suspend fun upDifficulty(subworkout: SubWorkoutModel): Resource<Unit> {
        val response = databaseRepository.getAllExercisesBySubWorkoutId(subworkout.id)
        if ((response is Resource.Success) && (response.data != null) ){
            val exercises = response.data.toMutableList()
            exercises.forEach {
                if (it.time != null) {
                    it.time = it.time!!.toLong() + (10 * 1000).toLong()
                } else{
                    it.repetitionsCount = it.repetitionsCount!! + 2
                }
                databaseRepository.updateSubWorkoutExercise(it)
            }
            subworkout.difficulty = 0
            return databaseRepository.updateSubWorkout(subworkout)
        }
        return Resource.Error(RuntimeException(),"Error with up difficulty")
    }


}