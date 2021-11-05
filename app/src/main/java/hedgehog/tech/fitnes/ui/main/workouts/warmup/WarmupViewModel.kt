package hedgehog.tech.fitnes.ui.main.workouts.warmup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.*
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class WarmupViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val user = MutableLiveData<Resource<UserModel>>()
    val subWorkoutExercises = MutableLiveData<Resource<List<SubWorkoutExerciseModel>>>()
    val exercise = MutableLiveData<Resource<ExerciseModel>>()
    val isSuccessInsertSubWorkoutHistory = MutableLiveData<Resource<Unit>>()
    val subWorkout = MutableLiveData<Resource<SubWorkoutModel>>()
    val workout = MutableLiveData<Resource<WorkoutModel>>()

    fun getUser() = viewModelScope.launch {
        user.postValue(databaseRepository.getUser())
    }

    fun getAllExercisesBySubWorkoutId(id: Int) = viewModelScope.launch {
        subWorkoutExercises.postValue(databaseRepository.getAllExercisesBySubWorkoutId(id))
    }

    fun getExerciseById(id: Int) = viewModelScope.launch {
        exercise.postValue(databaseRepository.getExerciseById(id))
    }

    fun getSubworkoutById(id: Int) = viewModelScope.launch {
        subWorkout.postValue(databaseRepository.getSubWorkoutById(id))
    }
}