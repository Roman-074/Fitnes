package hedgehog.tech.fitnes.ui.main.workouts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Event
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {
    val workouts = MutableLiveData<Resource<List<WorkoutModel>>>()
    val user = MutableLiveData<Resource<UserModel>>()

    fun getUser() = viewModelScope.launch{
        user.postValue(databaseRepository.getUser())
    }

    fun getWorkouts() = viewModelScope.launch{
        workouts.postValue(databaseRepository.getWorkouts())
    }




}