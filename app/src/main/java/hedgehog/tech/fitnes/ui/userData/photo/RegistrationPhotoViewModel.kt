package hedgehog.tech.fitnes.ui.userData.photo

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
class RegistrationPhotoViewModel @Inject constructor(
    private val dbRepository: DatabaseRepository
) : ViewModel() {
    val isStandartPhoto = MutableLiveData<Resource<Boolean>>()
    val photoPath = MutableLiveData<Resource<String>>()
    val photoIndex = MutableLiveData<Resource<Int>>()
    val isSetPhotoFromGallery = MutableLiveData<Resource<Unit>>()

    fun isStandartPhoto() = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        isStandartPhoto.postValue(Resource.Success(user.isStandartPhoto))
    }

    fun getPathToPhoto() = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        photoPath.postValue(Resource.Success(user.pathToPhoto))
    }

    fun getPhotoIndex() = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        photoIndex.postValue(Resource.Success(user.photoIndex))
    }

    fun setPathToPhoto(path: String) = viewModelScope.launch {
        val user: UserModel = dbRepository.getUser().data!!
        user.pathToPhoto = path
        user.isStandartPhoto = false
        isSetPhotoFromGallery.postValue(dbRepository.insertUser(user))
        getPathToPhoto()
    }
}