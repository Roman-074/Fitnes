package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Event
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class ReminderDialogViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val isSuccessInsert = MutableLiveData<Event<Resource<Unit>>>()

    fun insertReminder(reminder:ReminderModel) = viewModelScope.launch {
        isSuccessInsert.postValue(Event(databaseRepository.insertReminder(reminder)))
    }
}