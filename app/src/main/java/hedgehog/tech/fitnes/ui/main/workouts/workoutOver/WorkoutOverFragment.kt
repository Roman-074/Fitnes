package hedgehog.tech.fitnes.ui.main.workouts.workoutOver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentWorkoutOverBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class WorkoutOverFragment : BaseFragment() {

    private var _binding: FragmentWorkoutOverBinding? = null
    private val binding get() = _binding!!

    private val args: WorkoutOverFragmentArgs by navArgs()
    private val viewmodel: WorkoutOverViewModel by viewModels()
    lateinit var user: UserModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOverBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewmodel.getUser()
    }

    private fun initOnBackPressed() {
        // при нажатии кнопки назад тыкаем на кнопку, так как действия совпадают
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            binding.repeatBtn.performClick()
        }
    }

    private fun initObservers() {
        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        user = it
                        viewmodel.getLastWorkoutHistory()
                    }
                }
                is Resource.Error -> SnackbarUtils.showShortSnackbar(
                    this.resources.getString(R.string.error),
                    requireView()
                )
            }
        })

        viewmodel.lastWorkoutHistory.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    initUI(user, result.data!!)
                    viewmodel.getSubworkoutById(args.subworkoutId)
                }
                is Resource.Error -> SnackbarUtils.showShortSnackbar(
                    this.resources.getString(R.string.error),
                    requireView()
                )
            }
        })
        viewmodel.subworkout.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        initOnBackPressed()
                        initButtons(it)
                    }
                }
                is Resource.Error -> SnackbarUtils.showShortSnackbar(
                    this.resources.getString(R.string.error),
                    requireView()
                )
            }
        })
        viewmodel.isSuccessUpdateDifficulty.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    findNavController().popBackStack()
                    viewmodel.isSuccessUpdateDifficulty.value = null
                }
                is Resource.Error -> SnackbarUtils.showShortSnackbar(
                    this.resources.getString(R.string.error),
                    requireView()
                )
            }
        })
    }
    //заносим данные последней тренировки во вьюхи
    private fun initUI(user: UserModel, subWorkoutHistoryModel: SubWorkoutHistoryModel) {
        val exercisesCount = subWorkoutHistoryModel.progress.filter { it.value }.size
        binding.exercisesCount.text = exercisesCount.toString()
        val simpleDateFormatter = SimpleDateFormat("mm:ss", resources.configuration.locale)
        binding.durationCount.text = simpleDateFormatter.format(Date(args.time))
        binding.score.text = getString(
            R.string.you_get_num_points,
            UserScoreUtil.setUserScore(exercisesCount, user.activityLevel)
        )
        binding.currWeight.text =
            getString(R.string.workout_over_kg, user.currWeight!!.roundToInt().toString())
        binding.bmi.text = getString(
            R.string.workout_over_bmi,
            WeightUtils.getBmi(user.currWeight ?: 0F, user.tall ?: 0F).toString()
        )
    }

    private fun initButtons(subworkout: SubWorkoutModel) {
        binding.repeatBtn.setOnClickListener {
            //смотрим, нужно ли увеличивать или уменьшать количество повторений в упражнениях
            calculateDifficulty(subworkout)
        }
        binding.icGood.setOnClickListener {
            binding.icGood.apply {
                setImageResource(R.drawable.workout_over_icon_good_active)
                isClickable = false
                isFocusable = false
            }
            binding.icBad.apply {
                setImageResource(R.drawable.workout_over_icon_bad_unactive)
                isClickable = true
                isFocusable = true
            }
            binding.icGreat.apply {
                setImageResource(R.drawable.workout_over_icon_great_unactive)
                isClickable = true
                isFocusable = true
            }
        }

        binding.icBad.setOnClickListener {
            binding.icGood.apply {
                setImageResource(R.drawable.workout_over_icon_good_unactive)
                isClickable = true
                isFocusable = true
            }
            binding.icBad.apply {
                setImageResource(R.drawable.workout_over_icon_bad_active)
                isClickable = false
                isFocusable = false
            }
            binding.icGreat.apply {
                setImageResource(R.drawable.workout_over_icon_great_unactive)
                isClickable = true
                isFocusable = true
            }
        }

        binding.icGreat.setOnClickListener {
            binding.icGood.apply {
                setImageResource(R.drawable.workout_over_icon_good_unactive)
                isClickable = true
                isFocusable = true
            }
            binding.icBad.apply {
                setImageResource(R.drawable.workout_over_icon_bad_unactive)
                isClickable = true
                isFocusable = true
            }
            binding.icGreat.apply {
                setImageResource(R.drawable.workout_over_icon_great_active)
                isClickable = false
                isFocusable = false
            }
        }
        //по умолчанию выбираем good
        binding.icGood.performClick()


        binding.editCurrWeight.setOnClickListener {
            findNavController().navigate(
                WorkoutOverFragmentDirections.actionWorkoutOverFragmentToEditCurrWeightFragment()
            )
        }
        binding.reminder.setOnClickListener {
            findNavController().navigate(
                WorkoutOverFragmentDirections.actionWorkoutOverFragmentToReminderFragment(args.workoutId)
            )
        }


    }

    private fun calculateDifficulty(subworkout: SubWorkoutModel) {
        //проверяем выбранное состояние и либо прибавляем, либо убавляем счетчик
        var newDifficulty = subworkout.difficulty
        if (!binding.icBad.isClickable) {
            newDifficulty--
        } else if (!binding.icGreat.isClickable) {
            newDifficulty++
        } else {
            findNavController().popBackStack()
            return
        }
        viewmodel.changeDifficulty(subworkout,newDifficulty)
    }


}