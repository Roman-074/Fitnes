package hedgehog.tech.fitnes.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.impl.model.PreferenceDao
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.BuildConfig
import hedgehog.tech.fitnes.databinding.ActivityOnboardingBinding
import hedgehog.tech.fitnes.ui.main.MainActivity
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity



@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initButtons()

    }

    private fun initButtons() {
        binding.startBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()

        }
    }

}