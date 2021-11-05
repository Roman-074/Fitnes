package hedgehog.tech.fitnes.ui.main.progress

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.*
import hedgehog.tech.fitnes.data.models.SubWorkoutModel.Companion.ADVANCED
import hedgehog.tech.fitnes.data.models.SubWorkoutModel.Companion.MASTER
import hedgehog.tech.fitnes.data.models.SubWorkoutModel.Companion.WEAK
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val user = MutableLiveData<Resource<UserModel>>()
    val completedExercises = MutableLiveData<Resource<ExerciseStatisticModel>>()
    val totalScore = MutableLiveData<Resource<Int>>()

    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    //берем статистику выполненых упражнений
    fun getCompletedExerciseStatistics() = viewModelScope.launch {
        //если в лайвдате состояния Loading, то ретурнимся
        completedExercises.value?.let {
            if (it is Resource.Loading) {
                return@launch
            }
        }
        completedExercises.postValue(Resource.Loading())
        completedExercises.postValue(databaseRepository.getCompletedExerciseStatistics())
    }

    fun getCurrentTotalScore() = viewModelScope.launch {
        totalScore.value?.let {
            if (it is Resource.Loading) {
                return@launch
            }
        }
        totalScore.postValue(Resource.Loading())
        var totalScoreCount = 0
        val subworkoutHistoryList = databaseRepository.getSubWorkoutsHistorySortedByReverseDate()
        if (subworkoutHistoryList is Resource.Success && subworkoutHistoryList.data != null) {
            subworkoutHistoryList.data.forEach {
                val resourceSubWorkout = databaseRepository.getSubWorkoutById(it.subWorkoutId)
                if (resourceSubWorkout is Resource.Success && resourceSubWorkout.data != null) {
                    it.progress.forEach { row ->
                        if (row.value) {
                            totalScoreCount += when (resourceSubWorkout.data.hardType) {
                                WEAK -> 1
                                ADVANCED -> 2
                                MASTER -> 3
                                else -> 1
                            }

                        }
                    }
                } else {
                    totalScore.postValue(Resource.Error())
                    return@launch
                }
            }
        } else {
            totalScore.postValue(Resource.Error())
            return@launch
        }

        totalScore.postValue(Resource.Success(totalScoreCount))

    }


}