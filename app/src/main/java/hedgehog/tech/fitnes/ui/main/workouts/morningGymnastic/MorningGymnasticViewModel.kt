package hedgehog.tech.fitnes.ui.main.workouts.morningGymnastic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject




@HiltViewModel
class MorningGymnasticViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val subworkouts = MutableLiveData<Resource<List<SubWorkoutModel>>>()
    val subWorkoutExercises = MutableLiveData<Resource<List<SubWorkoutExerciseModel>>>()
    val user = MutableLiveData<Resource<UserModel>>()

    fun getUser() = viewModelScope.launch{
        user.postValue(databaseRepository.getUser())
    }

    fun getSubWorkoutsByWorkoutId(id:Int) = viewModelScope.launch{
        subworkouts.postValue(databaseRepository.getSubWorkoutsByWorkoutId(id,SubWorkoutModel.ALL))
    }

    fun getAllExercisesBySubWorkoutId(id: Int)  = viewModelScope.launch{
        subWorkoutExercises.postValue(databaseRepository.getAllExercisesBySubWorkoutId(id))
    }




}