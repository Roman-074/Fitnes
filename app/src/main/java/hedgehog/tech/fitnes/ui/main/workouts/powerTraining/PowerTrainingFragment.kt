package hedgehog.tech.fitnes.ui.main.workouts.powerTraining

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
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.UserScoreUtil
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentMorningGymnasticBinding
import hedgehog.tech.fitnes.databinding.FragmentPowerTrainingBinding
import javax.inject.Inject


@AndroidEntryPoint
class PowerTrainingFragment : BaseFragment() {

    private var _binding: FragmentPowerTrainingBinding? = null
    private val binding get() = _binding!!


    private val viewmodel: PowerTrainingViewModel by viewModels()
    private val args: PowerTrainingFragmentArgs by navArgs()

    private lateinit var powerTrainingAdapter: PowerTrainingAdapter

    private lateinit var user: UserModel

    @Inject
    lateinit var glide: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPowerTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initAdapters(binding.recycleView)
        initToolbar()
        viewmodel.getUser()
        binding.subWorkoutGroup.isVisible = true
        binding.restDayGroup.isVisible = false
    }

    private fun initButtons(subWorkoutId: Int) {
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                PowerTrainingFragmentDirections
                    .actionPowerTrainingFragmentToExerciseInfoFragment(
                        subWorkoutId,
                        args.workoutModelId,
                        user.gender!!,
                        isChangeProgress = false,
                        isExistWarmUp = false
                    )
            )
        }
    }


    private fun initToolbar() {
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun initAdapters(rv: RecyclerView) {
        powerTrainingAdapter = PowerTrainingAdapter()
        rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = powerTrainingAdapter
        }
    }

    private fun initObservers() {

        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        it.activityLevel?.let { level ->
                            user = it
                            if (level == "weak"){
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.beginner))
                            } else{
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, level)
                            }
                        }
                        if (it.isStandartPhoto!!) {
                            user.photoIndex?.let {index ->
                                glide.load(Constants.STANDART_PHOTOS[index])
                                    .into(binding.toolbarLayout.profilePhoto)
                            }
                        } else {
                            glide.load(it.pathToPhoto).into(binding.toolbarLayout.profilePhoto)
                        }
                        //берем подтренировку, согласно сложности пользователя

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
                            else -> snackbar(getString(R.string.error))
                        }

                    }
                }
                is Resource.Error -> {
                    snackbar(getString(R.string.error))
                }
            }
        })
        viewmodel.subworkouts.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.get(0)?.let {
                        viewmodel.getAllExercisesBySubWorkoutId(it.id)
                        initButtons(it.id)
                    }
                }
                is Resource.Error -> {
                    snackbar(getString(R.string.error))
                }
            }
        })

        viewmodel.subWorkoutExercises.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        powerTrainingAdapter.subWorkoutExercises = it
                        binding.exercises.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                powerTrainingAdapter.itemCount,
                                powerTrainingAdapter.itemCount
                            )
                        binding.points.text =
                            resources.getQuantityString (
                                R.plurals.num_points,
                                UserScoreUtil.setUserScore(
                                    powerTrainingAdapter.itemCount,
                                    user.activityLevel
                                ).toInt(),
                                UserScoreUtil.setUserScore(
                                    powerTrainingAdapter.itemCount,
                                    user.activityLevel
                                ).toInt()
                            )
                    }
                }
                is Resource.Error -> {
                    snackbar(getString(R.string.error))
                }
            }
        })


    }


}