package hedgehog.tech.fitnes.ui.main.workouts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentWorkoutsBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutsFragment : BaseFragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: WorkoutsViewModel by viewModels()

    private lateinit var workoutsAdapter: WorkoutsAdapter

    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.backBtn.isVisible = false
        initObservers()
        initAdapters(binding.workoutsRv)
        viewmodel.getWorkouts()
        viewmodel.getUser()
    }


    private fun initAdapters(workoutsRv: RecyclerView) {
        workoutsAdapter = WorkoutsAdapter()
            //Переходим на нужную тренировку

        workoutsRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutsAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        workoutsAdapter.setOnWorkoutClickListener { workout ->
            when (workout.type) {
                WorkoutModel.TYPE_GYMNASTIC -> {
                    Timber.d(workout.type.toString())
                    findNavController().navigate(
                        WorkoutsFragmentDirections
                            .actionWorkoutsFragmentToMorningGymnasticFragment(workout.id)
                    )
                    workoutsAdapter.setOnWorkoutClickListener {  }
                }
                WorkoutModel.TYPE_7_DAYS_TRAINING_COURSE -> {

                    Timber.d(workout.type.toString())

                    findNavController().navigate(
                        WorkoutsFragmentDirections
                            .actionWorkoutsFragmentToSevenDaysTrainingCourseFragment(workout.id)
                    )
                    workoutsAdapter.setOnWorkoutClickListener {  }
                }
                WorkoutModel.TYPE_SPECIFIC_MUSCLE_GROUPS -> {
                    Timber.d(workout.type.toString())

                    findNavController().navigate(
                        WorkoutsFragmentDirections
                            .actionWorkoutsFragmentToWorkoutsForSpecificMuscleGroupsFragment(workout.id)
                    )
                    workoutsAdapter.setOnWorkoutClickListener {  }
                }
                WorkoutModel.TYPE_POWER_TRAINING -> {
                    Timber.d(workout.type.toString())

                    findNavController().navigate(
                        WorkoutsFragmentDirections
                            .actionWorkoutsFragmentToPowerTrainingFragment(workout.id)
                    )
                    workoutsAdapter.setOnWorkoutClickListener {  }
                }
                WorkoutModel.TYPE_HIGH_BAR -> {
                    Timber.d(workout.type.toString())

                    findNavController().navigate(
                        WorkoutsFragmentDirections
                            .actionWorkoutsFragmentToHorizontalBarStartTestFragment(workout.id)
                    )
                    workoutsAdapter.setOnWorkoutClickListener {  }
                }
            }
        }

    }

    private fun initObservers() {
        viewmodel.workouts.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        workoutsAdapter.workouts = it
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })



        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        //заносим данные в тулбар
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
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })


    }
}