package hedgehog.tech.fitnes.ui.main.workouts.horizontalBar.horizontalBar

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
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentHorizontalBarBinding
import javax.inject.Inject


@AndroidEntryPoint
class HorizontalBarFragment : BaseFragment() {

    private var _binding: FragmentHorizontalBarBinding? = null
    private val binding get() = _binding!!


    private val viewmodel: HorizontalBarViewModel by viewModels()
    private val args: HorizontalBarFragmentArgs by navArgs()

    private lateinit var horizontalBarAdapter: HorizontalBarAdapter

    private lateinit var user: UserModel

    @Inject
    lateinit var glide: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHorizontalBarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initAdapters(binding.recycleView)
        initToolbar()
        viewmodel.getUser()
    }

    private fun initButtons(subWorkoutId: Int) {
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                HorizontalBarFragmentDirections
                    .actionHorizontalBarFragmentToExerciseInfoFragment(
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
        horizontalBarAdapter = HorizontalBarAdapter()
        rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = horizontalBarAdapter
        }
    }

    private fun initObservers() {

        viewmodel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        it.activityLevel?.let { level ->
                            user = it
                        }
                            //выставляем фото профиля
                        if (it.isStandartPhoto!!) {
                            user.photoIndex?.let {index ->
                                glide.load(Constants.STANDART_PHOTOS[index])
                                    .into(binding.toolbarLayout.profilePhoto)
                            }
                        } else {
                            glide.load(it.pathToPhoto).into(binding.toolbarLayout.profilePhoto)
                        }
                        viewmodel.getSubWorkoutsByWorkoutId(
                            args.workoutModelId,
                            args.subWorkoutType
                        )

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
                                //выставляем уровень сложности в туллбаре
                        when (it.hardType) {
                            SubWorkoutModel.WEAK -> {
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.beginner))
                            }
                            SubWorkoutModel.ADVANCED -> {
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.advanced))
                            }
                            SubWorkoutModel.MASTER -> {
                                binding.toolbarLayout.activityLevel.text =
                                    getString(R.string.level_num, getString(R.string.master))
                            }
                        }
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
                        horizontalBarAdapter.subWorkoutExercises = it
                        binding.exercises.text =
                        resources.getQuantityString (
                            R.plurals.num_exercises,
                            horizontalBarAdapter.itemCount,
                            horizontalBarAdapter.itemCount
                        )
                        binding.points.text = resources.getQuantityString (
                            R.plurals.num_points,
                            UserScoreUtil.setUserScore(
                                horizontalBarAdapter.itemCount,
                                user.activityLevel
                            ).toInt(),
                            UserScoreUtil.setUserScore(
                                horizontalBarAdapter.itemCount,
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