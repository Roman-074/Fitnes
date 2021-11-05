package hedgehog.tech.fitnes.repositories

import android.content.Context
import android.content.SharedPreferences
import hedgehog.tech.fitnes.app.BaseApp
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.UserModel
import timber.log.Timber


//Репозиторий для работы с shared preference
class PreferencesRepository {
    private val app:Context = BaseApp.getInstance()
    private val preferences: SharedPreferences =
        app.getSharedPreferences(app.packageName + "_preference", Context.MODE_PRIVATE)


    //функция для обертки кода в Resource
    private inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
        return try {
            action()
        } catch (e: Exception) {
            Timber.d(e)
            Resource.Error(e)
        }
    }

    //функция для записи в преференсы флага первого запуска приложения не устройстве
    fun setIsFirstLaunch(isFirstLaunch: Boolean) = safeCall {
        preferences.edit().putBoolean(PREFS_IS_FIRST_LAUNCH, isFirstLaunch).apply()
        Resource.Success<Unit>()
    }
    //функция для получения флага первого запуска приложения не устройстве
    fun isFirstLaunch() = safeCall {
        Resource.Success(preferences.getBoolean(PREFS_IS_FIRST_LAUNCH, true))

    }
    //функция для взятия нового неповторяющгося id alarm manager'а с автоинкрементов
    fun getAlarmServiceIndex(): Int {
        val index = preferences.getInt(PREFS_SERVICE_INDEX, 0)
        preferences.edit().putInt(PREFS_SERVICE_INDEX,index + 1).apply()
        return index
    }

    companion object {
        private const val PREFS_IS_FIRST_LAUNCH = "isFirstLaunch"
        private const val PREFS_SERVICE_INDEX = "serviceIndex"
    }
}