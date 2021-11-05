package hedgehog.tech.fitnes.di

import android.app.AlarmManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.data.dao.*
import hedgehog.tech.fitnes.data.database.AppDatabase
import hedgehog.tech.fitnes.repositories.DatabaseRepository
import hedgehog.tech.fitnes.repositories.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Singleton


//даггер хилт модуль хранящий все зависимости проекта
@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    lateinit var database: AppDatabase

    // провайды репозиториев

    @Singleton
    @Provides
    fun providePreferencesRepository() = PreferencesRepository()

    @Singleton
    @Provides
    fun provideDatabaseRepository(
        userDao: UserDao,
        workoutDao: WorkoutDao,
        exerciseDao: ExerciseDao,
        subWorkoutExerciseDao: SubWorkoutExerciseDao,
        subWorkoutHistoryDao: SubWorkoutHistoryDao,
        workoutHistoryDao: WorkoutHistoryDao,
        reminderDao: ReminderDao,
        subWorkoutDao: SubWorkoutDao,
    ) = DatabaseRepository(
        userDao,
        workoutDao,
        exerciseDao,
        subWorkoutExerciseDao,
        subWorkoutHistoryDao,
        workoutHistoryDao,
        reminderDao,
        subWorkoutDao
    )

    // провайды dao
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideExerciseDao(appDatabase: AppDatabase): ExerciseDao {
        return appDatabase.exerciseDao()
    }

    @Provides
    fun provideWorkoutDao(appDatabase: AppDatabase): WorkoutDao {
        return appDatabase.workoutDao()
    }

    @Provides
    fun provideWorkoutExerciseDao(appDatabase: AppDatabase): SubWorkoutExerciseDao {
        return appDatabase.workoutExerciseDao()
    }

    @Provides
    fun provideSubWorkoutHistoryDao(appDatabase: AppDatabase): SubWorkoutHistoryDao {
        return appDatabase.subWorkoutHistoryDao()
    }

    @Provides
    fun provideWorkoutHistoryDao(appDatabase: AppDatabase): WorkoutHistoryDao {
        return appDatabase.workoutHistoryDao()
    }

    @Provides
    fun provideReminderDao(appDatabase: AppDatabase): ReminderDao {
        return appDatabase.reminderDao()
    }

    @Provides
    fun provideSubWorkoutDao(appDatabase: AppDatabase): SubWorkoutDao {
        return appDatabase.subWorkoutDao()
    }

    // провайд БД
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context
    ): AppDatabase {
        database = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "AppDatabase"
        ).fallbackToDestructiveMigration()
            //коллбэк для занесения начальных данных в БД
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Timber.d("create database")
                    CoroutineScope(Dispatchers.IO).launch {
                        AppDatabase.onCreate(database)
                    }
                }
            })
            .build()
        return database
    }

    @Singleton
    @Provides
    //Провайд глайда
    fun provideGlide(@ApplicationContext context: Context) = Glide.with(context)

    @Provides
    //Провайд менеджера тревог
    fun provideAlarmManager(@ApplicationContext appContext: Context): AlarmManager {
        return appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    //Провайд билдера уведомлений
    fun provideNotificationBuilder(@ApplicationContext appContext: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(appContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.reminder_notification_service_img_src_calendar)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    appContext.resources,
                    R.drawable.reminder_notification_service_img_src_calendar
                )
            )
            .setAutoCancel(true)
            .setContentTitle(appContext.resources.getString(R.string.notification_content_title))
            .setContentText(appContext.resources.getString(R.string.notification_content_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    }


}