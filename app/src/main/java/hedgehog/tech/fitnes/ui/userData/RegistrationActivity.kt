package hedgehog.tech.fitnes.ui.userData

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.databinding.ActivityRegistrationBinding
import hedgehog.tech.fitnes.ui.main.MainActivity
import qiu.niorgai.BuildConfig
import qiu.niorgai.StatusBarCompat


@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity(), OnToolbarButtonPressed{

    lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

    }



    override fun onSkipButtonPressed(listener: View.OnClickListener) {
        binding.skipBtn.setOnClickListener(listener)
    }

    override fun onBackButtonPressed(listener: View.OnClickListener) {
        binding.backBtn.setOnClickListener(listener)
    }

}

//интерфейс для получения доступа к активити из фрагмента
interface OnToolbarButtonPressed {
    fun onSkipButtonPressed(listener: View.OnClickListener)
    fun onBackButtonPressed(listener: View.OnClickListener)
}