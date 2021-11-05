package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.editCurrWeighthedgehog.tech.fitnes.ui.main.workouts.workoutOver.editCurrWeight

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
class EditCurrWeightViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository
) : ViewModel() {
    val isSuccessSetCurrWeight = MutableLiveData<Resource<Unit>>()

    fun setCurWeight(weightInKg: Float) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.currWeight = weightInKg
        isSuccessSetCurrWeight.postValue(dbRepository.insertUser(user))
    }
}