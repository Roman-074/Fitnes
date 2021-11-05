package hedgehog.tech.fitnes.ui.main.workouts.sevenDaysTrainingCourse

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
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SevenDaysTrainingCourseFragment : BaseFragment() {

    private var _binding: FragmentSevenDaysTrainingCourseBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: SevenDaysTrainingCourseViewModel by viewModels()
    private val args: SevenDaysTrainingCourseFragmentArgs by navArgs()


    private lateinit var user: UserModel
    private lateinit var workoutHistoryModel: WorkoutHistoryModel
    private lateinit var subworkoutList: List<SubWorkoutModel>
    private lateinit var rvAdapter: SevenDaysTrainingCourseAdapter


    @Inject
    lateinit var glide: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSevenDaysTrainingCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initObservers()
        initButtons()
        initAdapter(binding.recycleView)
        viewmodel.getUser()
    }

    private fun initButtons() {
        binding.startBtn.setOnClickListener {
            //запускаем тут подтренировку, на которой остановились
            findNavController().navigate(
                SevenDaysTrainingCourseFragmentDirections
                    .actionSevenDaysTrainingCourseFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subworkoutList[workoutHistoryModel.subWorkoutProgress].id,
                        args.workoutModelId,
                        true,
                        1
                    )
            )
        }
    }

    private fun initAdapter(recycleView: RecyclerView) {
        rvAdapter = SevenDaysTrainingCourseAdapter()
        rvAdapter.setOnSubWorkoutClickListener { subWorkout,pos ->
            findNavController().navigate(
                SevenDaysTrainingCourseFragmentDirections
                    .actionSevenDaysTrainingCourseFragmentToSevenDaysTrainingCourseSubWorkoutFragment(
                        subWorkout.id,
                        args.workoutModelId,
                        false,
                        pos + 1
                    )
            )
        }
        recycleView.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        user = it
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
                        //показ тренировок на основе уровня игрока
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
                        subworkoutList = it
                        rvAdapter.subWorkouts = it
                    }
                    viewmodel.getWorkoutHistoryByWorkout(args.workoutModelId)
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })
        viewmodel.workoutHistory.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        workoutHistoryModel = it
                        rvAdapter.setProgress(it.subWorkoutProgress)
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

    override fun onResume() {
        super.onResume()
        viewmodel.getWorkoutHistoryByWorkout(args.workoutModelId)
    }


}