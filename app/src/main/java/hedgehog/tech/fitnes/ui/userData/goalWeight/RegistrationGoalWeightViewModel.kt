package hedgehog.tech.fitnes.ui.userData.goalWeight

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class RegistrationGoalWeightViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository
) : ViewModel() {
    val isSuccessSetGoalWeight = MutableLiveData<Resource<Unit>>()

    fun setGoalWeight(weightInKg: Float) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.goalWeight = weightInKg
        isSuccessSetGoalWeight.postValue(dbRepository.insertUser(user))
    }
}