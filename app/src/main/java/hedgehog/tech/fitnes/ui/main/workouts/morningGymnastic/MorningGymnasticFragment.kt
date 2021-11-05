package hedgehog.tech.fitnes.ui.main.workouts.morningGymnastic

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
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.UserScoreUtil
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentMorningGymnasticBinding
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MorningGymnasticFragment : BaseFragment() {

    private var _binding: FragmentMorningGymnasticBinding? = null
    private val binding get() = _binding!!


    private val viewmodel: MorningGymnasticViewModel by viewModels()
    private val args: MorningGymnasticFragmentArgs by navArgs()

    private lateinit var morningGymnasticAdapter: MorningGymnasticAdapter

    private lateinit var user: UserModel

    @Inject
    lateinit var glide: RequestManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMorningGymnasticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initAdapters(binding.recycleView)
        initToolbar()
        viewmodel.getSubWorkoutsByWorkoutId(args.workoutModelId)
        viewmodel.getUser()
    }

    private fun initButtons(subWorkoutId: Int) {
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                MorningGymnasticFragmentDirections
                    .actionMorningGymnasticFragmentToExerciseInfoFragment(
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
        morningGymnasticAdapter = MorningGymnasticAdapter()
        rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = morningGymnasticAdapter
        }
    }

    private fun initObservers() {
        viewmodel.subworkouts.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    Timber.d(result.toString())
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
                        morningGymnasticAdapter.subWorkoutExercises = it
                        binding.exercises.text =
                            resources.getQuantityString (
                                R.plurals.num_exercises,
                                morningGymnasticAdapter.itemCount,
                                morningGymnasticAdapter.itemCount
                            )


                        binding.points.text =
                            resources.getQuantityString (
                                R.plurals.num_points,
                                UserScoreUtil.setUserScore(
                                    morningGymnasticAdapter.itemCount,
                                    user.activityLevel
                                ).toInt(),
                                UserScoreUtil.setUserScore(
                                    morningGymnasticAdapter.itemCount,
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
                            user.photoIndex?.let {index ->
                                glide.load(Constants.STANDART_PHOTOS[index])
                                    .into(binding.toolbarLayout.profilePhoto)
                            }
                        } else {
                            glide.load(it.pathToPhoto).into(binding.toolbarLayout.profilePhoto)
                        }
                    }
                }
                is Resource.Error -> {
                    snackbar("Error")
                }
            }
        })
    }


}