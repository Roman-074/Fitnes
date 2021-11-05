package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val reminders = MutableLiveData<Resource<List<ReminderModel>>>()
    val isSuccessChangeEnabledStatus = MutableLiveData<Resource<Unit>>()
    val isSuccessDelete = MutableLiveData<Resource<Unit>>()

    fun getRemindersByWorkoutId(workoutId: Int) = viewModelScope.launch {
        reminders.postValue(databaseRepository.getRemindersByWorkoutId(workoutId))
    }

    fun changeEnableStatusOfReminderById(reminderId: Int, isEnable: Boolean) =
        viewModelScope.launch {
            isSuccessChangeEnabledStatus.postValue(
                databaseRepository.changeEnableStatusOfReminderById(
                    reminderId,
                    isEnable
                )
            )

        }

            //удаляем напоминалку в БД и сервисе
    fun deleteReminder(
        context: Context,
        reminder: ReminderModel,
        workoutId: Int,
        alarmManager: AlarmManager
    ) = viewModelScope.launch {
        val intent = Intent(context, ReminderNotificationBroadcastReceiver::class.java)

        reminder.alarmServiceIdList.forEach { id ->
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
        databaseRepository.deleteReminder(reminder.id)
        getRemindersByWorkoutId(workoutId)

    }

    fun insertReminder(reminder: ReminderModel, workoutId: Int) = viewModelScope.launch {
        isSuccessDelete.postValue(databaseRepository.insertReminder(reminder))
        getRemindersByWorkoutId(workoutId)
    }
}