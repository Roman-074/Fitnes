package hedgehog.tech.fitnes.ui.main.workouts.sevenDaysTrainingCourse.sevenDaysTrainingCourseSubWorkout

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentSevenDaysTrainingCourseSubworkoutBinding
import javax.inject.Inject


@AndroidEntryPoint
class SevenDaysTrainingCourseSubWorkoutFragment :
    BaseFragment() {

    private var _binding: FragmentSevenDaysTrainingCourseSubworkoutBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: SevenDaysTrainingCourseSubWorkoutViewModel by viewModels()
    private val args: SevenDaysTrainingCourseSubWorkoutFragmentArgs by navArgs()

    private lateinit var sevenDaysTrainingCourseSubWorkoutAdapter: SevenDaysTrainingCourseSubWorkoutAdapter

    private lateinit var userModel: UserModel


    @Inject
    lateinit var glide: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentSevenDaysTrainingCourseSubworkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startWithoutWarmUpBtn.paintFlags =
            binding.startWithoutWarmUpBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        initObservers()
        viewmodel.getSubWorkoutById(args.subWorkoutId)
    }

    private fun initAdapters(recycleView: RecyclerView) {
        sevenDaysTrainingCourseSubWorkoutAdapter = SevenDaysTrainingCourseSubWorkoutAdapter()
        recycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sevenDaysTrainingCourseSubWorkoutAdapter
        }
    }

    private fun initRestFragment() {
        binding.subWorkoutGroup.isVisible = false
        binding.restDayGroup.isVisible = true
        binding.restDayFragment.startBtn.setOnClickListener {
            viewmodel.incrementWorkoutHistoryProgressByWorkoutId(args.workoutModelId)
        }
        binding.restDayFragment.toolbarLayout.restTitle.text = getString(R.string.day_num_rest_day,args.dayNumber)

    }

    private fun initSubWorkoutFragment(title: String) {
        //выставляем иконку изображения
        Constants.SEVEN_DAYS_COURSE_SUBWORKOUTS_ICONS.forEach {
            if (getString(it.key) == title) {
                binding.exerciseIcon.setImageResource(it.value)
                return@forEach
            }
        }
        //скрываем верстку с отдыхом
        binding.subWorkoutGroup.isVisible = true
        binding.restDayGroup.isVisible = false
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        initAdapters(binding.recycleView)
        viewmodel.getUser()

        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                SevenDaysTrainingCourseSubWorkoutFragmentDirections
                    .actionSevenDaysTrainingCourseSubWorkoutFragmentToWarmupFragment(
                        args.subWorkoutId,
                        args.workoutModelId,
                        WorkoutModel.TYPE_7_DAYS_TRAINING_COURSE,
                        userModel.gender!!,
                        args.isChangeProgress
                    )
            )
        }
        binding.startWithoutWarmUpBtn.setOnClickListener {
            findNavController().navigate(
                SevenDaysTrainingCourseSubWorkoutFragmentDirections
                    .actionSevenDaysTrainingCourseSubWorkoutFragmentToExerciseInfoFragment(
                        args.subWorkoutId,
                        args.workoutModelId,
                        userModel.gender!!,
                        args.isChangeProgress,
                        true
                    )
            )
        }
    }

    private fun initObservers() {
        // отображение другой верстки, если день отдыха
        viewmodel.subWorkout.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        //TODO
                        if (it.title == "Rest day") {
                            initRestFragment()
                        } else {
                            initSubWorkoutFragment(it.title!!)
                        }
                        binding.title.text = it.title
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })

        viewmodel.isSuccessIncrementProgress.observe(viewLifecycleOwner, EventObserver {
            findNavController().popBackStack()
        })

        //взятие информации о пользователе из базы
        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        userModel = it
                        it.activityLevel?.let { level ->
                            if (level == "weak"){
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.beginner))
                            } else{
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, level)
                            }
                        }
                        if (it.isStandartPhoto!!) {
                            it.photoIndex?.let {index ->
                                glide.load(Constants.STANDART_PHOTOS[index])
                                    .into(binding.toolbarLayout.profilePhoto)
                            }
                        } else {
                            glide.load(it.pathToPhoto).into(binding.toolbarLayout.profilePhoto)
                        }
                        viewmodel.getAllExercisesBySubWorkoutId(args.subWorkoutId)
                    }

                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })

        //Взятие упражнений подтренировки
        viewmodel.subWorkoutExercises.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        sevenDaysTrainingCourseSubWorkoutAdapter.subWorkoutExercises = it
                        binding.exercises.text =  resources.getQuantityString (
                            R.plurals.num_exercises,
                            sevenDaysTrainingCourseSubWorkoutAdapter.itemCount,
                            sevenDaysTrainingCourseSubWorkoutAdapter.itemCount
                        )
                        binding.points.text = resources.getQuantityString (
                            R.plurals.num_points,
                            UserScoreUtil.setUserScore(
                                sevenDaysTrainingCourseSubWorkoutAdapter.itemCount,
                                userModel.activityLevel
                            ).toInt(),
                            UserScoreUtil.setUserScore(
                                sevenDaysTrainingCourseSubWorkoutAdapter.itemCount,
                                userModel.activityLevel
                            ).toInt()
                        )
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })

    }


}