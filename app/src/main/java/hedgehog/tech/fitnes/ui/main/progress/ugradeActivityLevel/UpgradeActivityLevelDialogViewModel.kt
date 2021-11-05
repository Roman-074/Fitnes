package hedgehog.tech.fitnes.ui.main.progress.ugradeActivityLevel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class UpgradeActivityLevelDialogViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val user = MutableLiveData<Resource<UserModel>>()
    val isSuccessSetLevel = MutableLiveData<Resource<Unit>>()

    fun getUser() = viewModelScope.launch{
        user.postValue(databaseRepository.getUser())
    }

    fun setActivityLevel(level: String)  = viewModelScope.launch{
        val userModel = databaseRepository.getUser().data
        userModel?.let {
            it.activityLevel = level
            isSuccessSetLevel.postValue(databaseRepository.insertUser(it))
        }
    }

}