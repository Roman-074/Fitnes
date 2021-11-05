package hedgehog.tech.fitnes.ui.main.progress

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.ExerciseStatisticModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.databinding.FragmentProgressBinding


@AndroidEntryPoint
class ProgressFragment : BaseFragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: ProgressViewModel by viewModels()

    private lateinit var statistic: ExerciseStatisticModel
    private lateinit var activityLevel: String
    private  var flag = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initButtons()
        viewmodel.getCompletedExerciseStatistics()
    }

    private fun initButtons() {
        binding.showProgressBtn.setOnClickListener {
            binding.exercisesContainer.isVisible = flag
            flag = !flag
        }
    }


    private fun initObservers() {
        //отслеживание бэкстэка после возвра  щения с диалогового окна для изменения уровня в прогрессе
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constants.PROGRESS_DIALOG_BACK_KEY)
            ?.observe(viewLifecycleOwner) {
                if (it) {
                    viewmodel.getCompletedExerciseStatistics()
                }
            }

        viewmodel.totalScore.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        binding.totalScore.text = it.toString()
                        initScoreUntilNextLevel(it)
                    }
                }
                is Resource.Error -> {
                    snackbar(getString(R.string.error))
                }
            }
        })

        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        activityLevel = it.activityLevel ?: "weak"
                        initUI(it)
                        viewmodel.getCurrentTotalScore()
                    }
                }
                is Resource.Error -> {
                    snackbar(getString(R.string.error))
                }
            }
        })
        viewmodel.completedExercises.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    viewmodel.getUser()
                    //выставляю количество упражнений
                    result.data?.let {
                        statistic = it
                        binding.totalExercises.text = it.countForAll.toString()
                        binding.workoutExercises.text = it.countForLastWorkout.toString()
                        binding.weekExercises.text = it.countForLastWeek.toString()
                        binding.monthExercises.text = it.countForLastMonth.toString()
                        binding.yearExercises.text = it.countForLastYear.toString()
                        binding.progressBar.isVisible = false
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false

                }
                //показываю прогрессбар во время загрузки данных из БД
                is Resource.Loading -> binding.progressBar.isVisible = true
            }
        })

        viewmodel

    }

    @SuppressLint("SetTextI18n")
    private fun initScoreUntilNextLevel(score: Int) {
        when (activityLevel) {
            "weak" -> {
                if (statistic.countForAll < 10) {
                    binding.image.setImageResource(R.drawable.progress_img_source_very_week_lion)
                } else {
                    binding.image.setImageResource(R.drawable.progress_img_source_week_lion)
                }
                if  (169 - score < 0){
                    binding.scoreUntilNextLevel.text = getString(R.string.you_can_go_the_next_level)
                } else{
                    binding.scoreUntilNextLevel.text = "${169 - score}"

                }
            }
            "advanced" -> {
                binding.scoreUntilNextLevel.text = "${507 - score}"
                if  (507 - score < 0){
                    binding.scoreUntilNextLevel.text = getString(R.string.you_can_go_the_next_level)
                } else{
                    binding.image.setImageResource(R.drawable.progress_img_source_advanced_lion)

                }

            }
            "master" -> {
                binding.scoreUntilNextLevel.isVisible = false
                binding.level.text = getString(R.string.master)
                binding.image.setImageResource(R.drawable.progress_img_source_master_lion)
            }
        }
    }

    //выставляю уровень в фрагменте
    private fun initUI(userModel: UserModel) {
        when (userModel.activityLevel) {
            "weak" -> {
                if (statistic.countForAll < 30) {
                    binding.image.setImageResource(R.drawable.progress_img_source_very_week_lion)
                } else {
                    binding.image.setImageResource(R.drawable.progress_img_source_week_lion)
                }
                binding.level.text = getString(R.string.beginner)
            }
            "advanced" -> {
                binding.level.text = getString(R.string.advanced)
                binding.image.setImageResource(R.drawable.progress_img_source_advanced_lion)
            }
            "master" -> {
                binding.level.text = getString(R.string.master)
                binding.image.setImageResource(R.drawable.progress_img_source_master_lion)
            }
        }
        userModel.activityLevel?.let {
            initLevelUpgrade(it)
        }
    }

    //показ окна смены уровня навыков
    private fun initLevelUpgrade(activityLevel: String) {
        if ((statistic.countForAll > 169) and (activityLevel != "advanced") and (activityLevel != "master")) {
            findNavController().navigate(
                ProgressFragmentDirections.actionProgressFragmentToUpgradeActivityLevelDialog(
                    statistic.countForAll
                )
            )
        } else if ((statistic.countForAll > 507) and (activityLevel != "master")) {
            findNavController().navigate(
                ProgressFragmentDirections.actionProgressFragmentToUpgradeActivityLevelDialog(
                    statistic.countForAll
                )
            )
        }
    }


}