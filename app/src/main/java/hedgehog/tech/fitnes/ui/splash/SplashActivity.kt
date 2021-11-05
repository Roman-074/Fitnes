package hedgehog.tech.fitnes.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.BuildConfig
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.app.utils.ToastUtils
import hedgehog.tech.fitnes.data.dao.UserDao
import hedgehog.tech.fitnes.data.database.AppDatabase
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.ui.main.MainActivity
import hedgehog.tech.fitnes.ui.onBoarding.OnBoardingActivity
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    //Это для дебага
    @Inject
    lateinit var appDatabase: AppDatabase


    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        subscribeObservers()

        //Это для дебага
        if (false) {
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase.clearAllTables()
                AppDatabase.onCreate(appDatabase)
                appDatabase.userDao().insert(
                    UserModel(
                        1,
                        "female",
                        "Zolax",
                        16,
                        176.0f,
                        65.0f,
                        null,
                        null,
                        true,
                        2,
                        "advanced"
                    )
                )
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        } else{
            viewModel.isFirstLaunch()

        }

        viewModel.isFirstLaunch()
    }

    //подписка на livedata
    private fun subscribeObservers() {
        viewModel.isFirstLaunch.observe(this, { result ->
            when (result) {
                is Resource.Success -> {
                    changeActivity(result.data ?: true)
                }
                is Resource.Error -> {
                    ToastUtils.showShortToast(this.resources.getString(R.string.error))
                }
            }
        })
        viewModel.isDatabaseCreate.observe(this, { result ->
            Timber.d("check observe")
            if (result) {
                //старт активити, если БД есть
                startActivity(Intent(this@SplashActivity, OnBoardingActivity::class.java))
                finish()
            } else {
                // взятие пользователя(для того чтобы взаимодействовать с dao чтобы даггер подтянул все зависимости, в том числе и БД)
                viewModel.getUser()
            }
        })

    }

    //смена активити после сплеша
    private fun changeActivity(isFirst: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            if (isFirst) {
                Timber.d("is first")
                viewModel.isDatabaseCreate()
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        }

    }


}