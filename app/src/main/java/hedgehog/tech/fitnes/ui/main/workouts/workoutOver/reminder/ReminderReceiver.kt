package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    val LOG_TAG = "myLogs"

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onReceive(context: Context?, intent: Intent) {
        Timber.d("onRecieve%s", intent.action);
    //Перезапускаем все напоминалки после перезагрузки телефона
        if ((Intent.ACTION_BOOT_COMPLETED == intent.action) and (!preferencesRepository.isFirstLaunch().data!!)) {
            context?.startService(Intent(context, ReminderLaunchAlarmsService::class.java))
        }
    }
}