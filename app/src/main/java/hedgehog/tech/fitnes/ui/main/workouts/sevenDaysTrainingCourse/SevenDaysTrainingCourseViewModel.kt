package hedgehog.tech.fitnes.ui.main.workouts.sevenDaysTrainingCourse

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Event
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutHistoryModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SevenDaysTrainingCourseViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val subworkouts = MutableLiveData<Resource<List<SubWorkoutModel>>>()
    val user = MutableLiveData<Resource<UserModel>>()
    val workoutHistory = MutableLiveData<Resource<WorkoutHistoryModel>>()

    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    fun getSubWorkoutsByWorkoutId(id: Int, type: Int) = viewModelScope.launch {
        subworkouts.postValue(databaseRepository.getSubWorkoutsByWorkoutId(id, type))
    }

    fun getWorkoutHistoryByWorkout(workoutModelId: Int) = viewModelScope.launch {
        val response = databaseRepository.getWorkoutHistoryByWorkout(workoutModelId)
        workoutHistory.postValue(response)

    }


}