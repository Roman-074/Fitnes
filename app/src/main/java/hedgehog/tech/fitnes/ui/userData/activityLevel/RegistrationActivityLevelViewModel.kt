package hedgehog.tech.fitnes.ui.userData.activityLevel

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
class RegistrationActivityLevelViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val isSuccessSetActivityLevel = MutableLiveData<Resource<Unit>>()

    fun setActivityLevel(level: String) = viewModelScope.launch{
        val user: UserModel = dbRepository.getUser().data!!
        user.activityLevel = level
        isSuccessSetActivityLevel.postValue(dbRepository.insertUser(user))
    }

    fun isFirstLaunch() = preferencesRepository.isFirstLaunch()
}