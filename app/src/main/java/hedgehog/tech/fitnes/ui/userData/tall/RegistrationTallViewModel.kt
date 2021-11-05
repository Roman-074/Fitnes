package hedgehog.tech.fitnes.ui.userData.tall

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
class RegistrationTallViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val isSuccessSetTall = MutableLiveData<Resource<Unit>>()

    fun setTall(tallInCm: Float) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.tall = tallInCm
        isSuccessSetTall.postValue(dbRepository.insertUser(user))
    }

    fun isFirstLaunch() = preferencesRepository.isFirstLaunch()

}