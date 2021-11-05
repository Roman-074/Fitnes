package hedgehog.tech.fitnes.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.database.AppDatabase
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: PreferencesRepository,
    private val dbRepository: DatabaseRepository
) : ViewModel() {
    val isFirstLaunch = MutableLiveData<Resource<Boolean>>()
    val isDatabaseCreate = MutableLiveData<Boolean>()
    val user = MutableLiveData<Resource<UserModel>>()


    fun isFirstLaunch(){
        isFirstLaunch.postValue(repository.isFirstLaunch())
    }

    //проверка создана ли БД, delay для того чтобы не вызывать эту функцию в фрагменте постоянно
     fun isDatabaseCreate() = viewModelScope.launch{
        delay(500)
        isDatabaseCreate.postValue(dbRepository.isDatabaseCreate())
    }

    fun getUser() = viewModelScope.launch{
        user.postValue(dbRepository.getUser())
        AppDatabase.isDatabaseCreate = true
        isDatabaseCreate()
    }



}