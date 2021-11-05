package hedgehog.tech.fitnes.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import qiu.niorgai.StatusBarCompat
import timber.log.Timber

//главная активити

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    public

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setupNavController(binding)
    }

    //настройка viewBinding
    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowTitleEnabled(false);
    }

    // настройка навигации
    private fun setupNavController(binding: ActivityMainBinding) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        initBottomNavBarItemSelectedListener(navController)
    }

    private fun initBottomNavBarItemSelectedListener(navController: NavController) {
        binding.bottomNavigationView.itemIconTintList = null
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            binding.appBarGroup.isVisible = arguments?.getBoolean("ShowAppBar",false) == true
            when(destination.id){
                R.id.profileFragment ->{
                    binding.bottomNavigationView.menu.getItem(0)
                        .setIcon(R.drawable.nav_menu_icon_progress_unactive)
                    binding.bottomNavigationView.menu.getItem(1)
                        .setIcon(R.drawable.nav_menu_icon_workout_unactive)
                    binding.bottomNavigationView.menu.getItem(2)
                        .setIcon(R.drawable.nav_menu_icon_profile_active)
                }
                R.id.progressFragment ->{
                    binding.bottomNavigationView.menu.getItem(0)
                        .setIcon(R.drawable.nav_menu_icon_progress_active)
                    binding.bottomNavigationView.menu.getItem(1)
                        .setIcon(R.drawable.nav_menu_icon_workout_unactive)
                    binding.bottomNavigationView.menu.getItem(2)
                        .setIcon(R.drawable.nav_menu_icon_profile_unactive)
                }
                R.id.workoutsFragment ->{
                    binding.bottomNavigationView.menu.getItem(0)
                        .setIcon(R.drawable.nav_menu_icon_progress_unactive)
                    binding.bottomNavigationView.menu.getItem(1)
                        .setIcon(R.drawable.nav_menu_icon_workout_active)
                    binding.bottomNavigationView.menu.getItem(2)
                        .setIcon(R.drawable.nav_menu_icon_profile_unactive)
                }
            }
        }
    }





}