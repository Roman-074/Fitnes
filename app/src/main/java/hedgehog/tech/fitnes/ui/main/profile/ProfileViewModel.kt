package hedgehog.tech.fitnes.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val user = MutableLiveData<Resource<UserModel>>()
    val history = MutableLiveData<Resource<List<List<SubWorkoutHistoryModel>>>>()

    fun getUser() = viewModelScope.launch{
        user.postValue(databaseRepository.getUser())
    }

    fun getAllSubWorkoutsHistorySortedByDate() = viewModelScope.launch{
        history.postValue(databaseRepository.getAllSubWorkoutsHistorySortedByDate())
    }

}