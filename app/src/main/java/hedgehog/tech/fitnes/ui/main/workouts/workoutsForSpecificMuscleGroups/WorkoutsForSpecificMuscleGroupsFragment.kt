package hedgehog.tech.fitnes.ui.main.workouts.workoutsForSpecificMuscleGroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import hedgehog.tech.fitnes.data.models.WorkoutHistoryModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentSevenDaysTrainingCourseBinding
import hedgehog.tech.fitnes.databinding.FragmentWorkoutsForSpecificMuscleGroupsBinding
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class WorkoutsForSpecificMuscleGroupsFragment : BaseFragment() {

    private var _binding: FragmentWorkoutsForSpecificMuscleGroupsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var glide: RequestManager

    private val viewmodel: WorkoutsForSpecificMuscleGroupsViewModel by viewModels()
    private val args: WorkoutsForSpecificMuscleGroupsFragmentArgs by navArgs()

    lateinit var subworkoutList: List<SubWorkoutModel>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentWorkoutsForSpecificMuscleGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initObservers()
        initButtons()
        viewmodel.getUser()
    }


    private fun initButtons() {
        //стартуем нужную подтренировку
        binding.armsAndChest.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_ARMS_AND_CHEST }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
        binding.press.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_PRESS }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
        binding.stretching.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_STRETCHING }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
        binding.legs.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_LEGS }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
        binding.plank.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_PLANK }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
        binding.shoulersAndBack.setOnClickListener {
            findNavController().navigate(
                WorkoutsForSpecificMuscleGroupsFragmentDirections
                    .actionWorkoutsForSpecificMuscleGroupsFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList.first { it.type == SubWorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS_SHOULDERS_AND_BACK }.id,
                        args.workoutModelId,
                        false,
                        dayNumber = 1
                    )
            )
        }
    }


    private fun initObservers() {
        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    //выставляем данные в тулбар
                    result.data?.let {
                        it.activityLevel?.let { level ->
                            if (level == "weak") {
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.beginner))
                            } else {
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

                        when (it.activityLevel) {
                            "weak" -> viewmodel.getSubWorkoutsByWorkoutId(
                                args.workoutModelId,
                                SubWorkoutModel.WEAK
                            )
                            "advanced" -> viewmodel.getSubWorkoutsByWorkoutId(
                                args.workoutModelId,
                                SubWorkoutModel.ADVANCED
                            )
                            "master" -> viewmodel.getSubWorkoutsByWorkoutId(
                                args.workoutModelId,
                                SubWorkoutModel.MASTER
                            )
                            else -> SnackbarUtils.showShortSnackbar(
                                this.resources.getString(R.string.error),
                                requireView()
                            )
                        }
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

        viewmodel.subworkouts.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        //выставляем упражнения подтренировкам
                        subworkoutList = it
                        binding.armsAndChestExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[0].exerciseCount!!,
                                it[0].exerciseCount
                            )
                        binding.pressExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[1].exerciseCount!!,
                                it[1].exerciseCount
                            )
                        binding.stretchingExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[2].exerciseCount!!,
                                it[2].exerciseCount
                            )
                        binding.legsExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[3].exerciseCount!!,
                                it[3].exerciseCount
                            )
                        binding.plankExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[4].exerciseCount!!,
                                it[4].exerciseCount
                            )
                        binding.shoulersAndBackExercisesCount.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                it[5].exerciseCount!!,
                                it[5].exerciseCount
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

    //тулбарчик
    private fun initToolbar() {
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}