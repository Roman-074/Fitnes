package hedgehog.tech.fitnes.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.database.AppDatabase
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class BaseApp : Application() {

    companion object {
        private lateinit var app: Application
        fun getInstance() = app
    }

    override fun onCreate() {
        super.onCreate()
        //создания дебаг дерева
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Timber.plant(Timber.DebugTree())
        app = this
        createNotificationChannel()
    }

    //Создание канала для уведомлений, если верся андроида больше 8ой
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "FitnessAppChannel"
            val descriptionText = "show notification of Fitness app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}