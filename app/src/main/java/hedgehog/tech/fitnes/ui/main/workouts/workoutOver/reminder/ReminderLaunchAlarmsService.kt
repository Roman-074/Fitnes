package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.DaysOfWeek
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.UnsupportedOperationException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class ReminderLaunchAlarmsService : Service() {


    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var databaseRepository: DatabaseRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onBind(intent: Intent): IBinder {
        throw   UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            initReminders()
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    //берем напоминалки из БД
    private suspend fun initReminders() {
        var reminders = databaseRepository.getReminders().data
            //создаем интент для alarm мменеджера
        val intent = Intent(baseContext, ReminderNotificationBroadcastReceiver::class.java)



        reminders?.forEach {
            //удаляем все запущенные напоминалки, чтобы не дублировать
            it.alarmServiceIdList.forEach { id ->
                val pendingIntent = PendingIntent.getBroadcast(
                    baseContext,
                    id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.cancel(pendingIntent)
            }
            it.alarmServiceIdList.clear()
            databaseRepository.insertReminder(it)
        }
        reminders = databaseRepository.getReminders().data


        reminders?.forEach {
            //если напоминлка в БД включена, выставляем на каждый день недели по ресиверу с повторением
            if (it.isEnable) {
                it.repetitions.forEach { daysOfWeek ->
                    when (daysOfWeek) {
                        DaysOfWeek.MONDAY -> {
                            //выставляем время
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                                //проверяем не выставлено ли время в прошлом
                            checkValidDate(calendar)
                                //стартуем сервис
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.TUESDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.WEDNESDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.THURSDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.FRIDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.SATURDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                        DaysOfWeek.SUNDAY -> {
                            val calendar = initCalendar(it.time)
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                            checkValidDate(calendar)
                            initAlarmService(calendar.timeInMillis, it)
                        }
                    }
                }
            }
        }
    }

    private suspend fun initAlarmService(timeInMillis: Long, reminderModel: ReminderModel) {
        val intent = Intent(baseContext, ReminderNotificationBroadcastReceiver::class.java)
        intent.putExtra(
            Constants.WORKOUT_TITLE_KEY,
            databaseRepository.getWorkoutById(reminderModel.workoutId).data?.title
        )
        intent.putExtra(
            Constants.WORKOUT_ID_KEY,
            databaseRepository.getWorkoutById(reminderModel.workoutId).data?.id
        )
            //стартуем 2 так как первый запуститься в первый раз а второй раз в 7 дней
        val serviceIndex = preferencesRepository.getAlarmServiceIndex()
        val serviceIndex2 = preferencesRepository.getAlarmServiceIndex()
        val pendingIntent = PendingIntent.getBroadcast(
            baseContext,
            serviceIndex,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingIntent2 = PendingIntent.getBroadcast(
            baseContext,
            serviceIndex2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (timeInMillis > Calendar.getInstance().timeInMillis) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent2
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent2
                )
            }
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            60000 * 60 * 24 * 7,
            pendingIntent
        )
        //добавляем id сервисов в БД, чтобы иметь возможность их остановить
        reminderModel.alarmServiceIdList.add(serviceIndex)
        reminderModel.alarmServiceIdList.add(serviceIndex2)
        databaseRepository.insertReminder(reminderModel)
        Timber.d("AlarmManager $alarmManager")
        Timber.d("Launch alarm in ${Date(timeInMillis)}")
    }

    private fun initCalendar(time: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    private fun checkValidDate(calendar: Calendar) {
        val now = Calendar.getInstance()
        now[Calendar.SECOND] = 0
        now[Calendar.MILLISECOND] = 0
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 7)
        }
    }


}