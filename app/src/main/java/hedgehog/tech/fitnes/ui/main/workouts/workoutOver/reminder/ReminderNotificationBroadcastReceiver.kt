package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.ui.main.MainActivity
import hedgehog.tech.fitnes.ui.main.workouts.morningGymnastic.MorningGymnasticFragmentArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class ReminderNotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var builder: NotificationCompat.Builder



    override fun onReceive(context: Context, intent: Intent?) {
        //делаем интент для перехода на нужный фрагмент при клике на уведомление
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.workoutsFragment)
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()
        builder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }


}


