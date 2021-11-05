package hedgehog.tech.fitnes.ui.userData.registrationComplete

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import javax.inject.Inject




@HiltViewModel
class RegistrationCompleteViewModel @Inject constructor(
    private val repository: PreferencesRepository
) : ViewModel() {

    fun setFirstLaunch(flag: Boolean){
        repository.setIsFirstLaunch(flag)
    }
}