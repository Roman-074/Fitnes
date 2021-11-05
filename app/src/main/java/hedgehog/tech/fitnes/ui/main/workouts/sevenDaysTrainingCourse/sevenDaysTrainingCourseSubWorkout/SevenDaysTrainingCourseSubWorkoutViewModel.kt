package hedgehog.tech.fitnes.ui.main.workouts.sevenDaysTrainingCourse.sevenDaysTrainingCourseSubWorkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Event
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SevenDaysTrainingCourseSubWorkoutViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val user = MutableLiveData<Resource<UserModel>>()
    val subWorkoutExercises = MutableLiveData<Resource<List<SubWorkoutExerciseModel>>>()
    val subWorkout = MutableLiveData<Resource<SubWorkoutModel>>()
    val isSuccessIncrementProgress = MutableLiveData<Event<Resource<Unit>>>()


    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    fun getAllExercisesBySubWorkoutId(id: Int) = viewModelScope.launch {
        subWorkoutExercises.postValue(databaseRepository.getAllExercisesBySubWorkoutId(id))
    }


    fun getSubWorkoutById(id: Int) = viewModelScope.launch {
        subWorkout.postValue(databaseRepository.getSubWorkoutById(id))
    }

    fun incrementWorkoutHistoryProgressByWorkoutId(id: Int) = viewModelScope.launch {
        val historyModelResource = databaseRepository.getWorkoutHistoryByWorkout(id)
        if (historyModelResource is Resource.Success ){
            historyModelResource.data?.let {
                it.subWorkoutProgress++
                isSuccessIncrementProgress.postValue(Event(databaseRepository.updateWorkoutHistory(it)))
                return@launch
            }
        }
        isSuccessIncrementProgress.postValue(Event(Resource.Error()))
    }




}