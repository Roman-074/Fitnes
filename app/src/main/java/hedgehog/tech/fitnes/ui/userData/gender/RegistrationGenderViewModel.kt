package hedgehog.tech.fitnes.ui.userData.gender

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
class RegistrationGenderViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository,
    private val preferencesRepository: PreferencesRepository

) : ViewModel() {
    val isSuccessSetGender = MutableLiveData<Resource<Unit>>()



    fun setGender(gender: String) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.gender = gender
        isSuccessSetGender.postValue(dbRepository.insertUser(user))
    }
    fun isFirstLaunch() = preferencesRepository.isFirstLaunch()

}