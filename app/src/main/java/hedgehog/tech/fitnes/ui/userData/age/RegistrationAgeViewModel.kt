package hedgehog.tech.fitnes.ui.userData.age

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
class RegistrationAgeViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val isSuccessSetAge = MutableLiveData<Resource<Unit>>()

    fun setAge(age: Int) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.age = age
        isSuccessSetAge.postValue(dbRepository.insertUser(user))
    }

    fun isFirstLaunch() = preferencesRepository.isFirstLaunch()
}