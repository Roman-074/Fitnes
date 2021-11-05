package hedgehog.tech.fitnes.ui.main.workouts.warmup

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieComposition
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentExerciseInfoBinding
import hedgehog.tech.fitnes.databinding.FragmentWarmupBinding
import timber.log.Timber
import java.util.*

//Комменты в ExerciseInfoFragment
@AndroidEntryPoint
class WarmupFragment : BaseFragment() {

    private var _binding: FragmentWarmupBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: WarmupViewModel by viewModels()
    private val args: WarmupFragmentArgs by navArgs()

    private var animFlag = true

    private var currTimer: CountDownTimer? = null

    private lateinit var exerciseSubs: List<SubWorkoutExerciseModel>
    private var currExerciseIndex = 0

    private var isShowInfo = false


    private lateinit var subWorkoutModel: SubWorkoutModel

    private var animList: List<Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWarmupBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressBar()
        initButtons()
        initObservers()

        initOnBackPressed()
        viewmodel.getAllExercisesBySubWorkoutId(Constants.WARM_UP_SUBWORKOUT_ID)
    }



    private fun initObservers() {
        //упражнения подтренировки
        viewmodel.subWorkoutExercises.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        exerciseSubs = it
                        initExercise(false)
                        viewmodel.getSubworkoutById(args.subWorkoutModelId)
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })

        //Загрузка модели подТренировки, для записи названия в историю
        viewmodel.subWorkout.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        subWorkoutModel = it
                        initExercise(false)
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
        //подробности упражения(для взятия описания)
        viewmodel.exercise.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        binding.exerciseDescription.text = it.description
                        //Выставляем анимацию упражнения
                        if (args.userGender == "male") {
                            changeExerciseAnim(it.maleAnimPath)
                        } else {
                            changeExerciseAnim(it.femaleAnimPath)
                        }
                        //меняем собержимое вьюх
                        changeExerciseUI()
                        viewmodel.exercise.value = null
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }


    //действие при нажатии кнопки назад
    private fun initOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isShowInfo) {
                        showExerciseVideo()
                    } else {
//                        if (currExerciseIndex == 0) {
//                            findNavController().popBackStack()
//                        } else {
//                            currExerciseIndex--
//                            initExercise()
//                        }
                        AlertDialogWithCustomViewProvider.provideAlertDialogWithCustomView(
                            requireContext(),
                            findNavController(),
                            layoutInflater
                        ).show()
                    }
                }
            })
    }

    //показ конкретной тренировки
    private fun initExercise(isResumed: Boolean) {
        if (currExerciseIndex >= exerciseSubs.size && exerciseSubs.isNotEmpty()) {
            finishWorkout()
        } else {
            if (!isResumed) {
                viewmodel.getExerciseById(exerciseSubs[currExerciseIndex].exerciseId)
            }

        }
    }

    private fun changeExerciseAnim(path: String) {
        if (path.isEmpty()) {
            snackbar(getString(R.string.error_animation))
        } else {
            LottieComposition.Factory.fromAssetFileName(requireContext(), path) { composition ->
                composition?.let {
                    binding.animatedExercise.setComposition(composition)
                    binding.animatedExercise.playAnimation()
                }
            }

        }

    }

    //Изменяем данные упражнения
    private fun changeExerciseUI() {
        binding.toolbarLayout.exerciseCount.text =
            getString(R.string.warm_up)
        binding.exerciseTitle.text = exerciseSubs[currExerciseIndex].title
        exerciseSubs[currExerciseIndex].repetitionsCount?.let {
            binding.exerciseRepetitions.text = getString(R.string.x, it)
            binding.exerciseCountInfo.text = resources.getQuantityString(R.plurals.repeat_num_times, it,it)
            binding.exerciseRepetitions.isVisible = true
        }
        if (exerciseSubs[currExerciseIndex].approachesCount == null) {
            binding.approachesIv.isVisible = false
            binding.approachesTv.isVisible = false
        } else {
            exerciseSubs[currExerciseIndex].approachesCount.let {
                binding.approachesIv.isVisible = true
                binding.approachesTv.isVisible = true
                binding.approachesTv.text = it.toString()
                binding.exerciseCountInfo.text =
                    getString(R.string.aproaches_num, binding.exerciseCountInfo.text.toString(), it)
            }
        }
        if (exerciseSubs[currExerciseIndex].time != null) {
            binding.approachesIv.isVisible = false
            binding.approachesTv.isVisible = false
            binding.exerciseRepetitions.isVisible = false
        }
    }


    private fun finishWorkout() {
        findNavController().navigate(
            WarmupFragmentDirections.actionWarmupFragmentToExerciseInfoFragment(
                args.subWorkoutModelId,
                args.workoutModelId,
                args.userGender,
                args.isChangeProgress,
                isExistWarmUp = true
            )
        )
    }


    private fun initButtons() {
        binding.exercisePlayButton.setOnClickListener {
            if (animFlag) {
                //если нет времени у упражнения, то при кнопке запуска, сразу переходить к следующему
                if (exerciseSubs[currExerciseIndex].time == null) {
                    nextExercise()
                } else {
                    startProgressBar(exerciseSubs[currExerciseIndex].time!!)
                }
            } else {
                resetProgressBar()
            }
        }
        binding.exerciseEnd.setOnClickListener {
            AlertDialogWithCustomViewProvider.provideAlertDialogWithCustomView(
                requireContext(),
                findNavController(),
                layoutInflater
            ).show()
        }
        binding.exerciseSkip.setOnClickListener {
            resetProgressBar()
            nextExercise()
        }
        //действия при окончании прогресс бара
        binding.progressBar.onProgressChangeListener = { progress ->
            if (progress == 100f) {
                resetProgressBar()
                nextExercise()

            }
        }
        binding.toolbarLayout.infoBtn.setOnClickListener {
            showExerciseInfo()
        }

        binding.toolbarLayout.videoBtn.setOnClickListener {
            showExerciseVideo()
        }
        binding.toolbarLayout.backBtn.setOnClickListener {
            if (isShowInfo) {
                showExerciseVideo()
            } else {
                if (currExerciseIndex == 0) {
                    findNavController().popBackStack()
                } else {
                    previousExercise()
                }
            }

        }
    }

    private fun nextExercise() {
        currExerciseIndex++
        initExercise(false)
    }

    private fun previousExercise() {
        currExerciseIndex--
        initExercise(false)
    }

    private fun showExerciseVideo() {
        binding.videoGroup.isVisible = true
        binding.toolbarLayout.infoBtn.isVisible = true
        binding.infoGroup.isVisible = false
        binding.toolbarLayout.videoBtn.isVisible = false
        isShowInfo = false
        initExercise(true)
    }

    private fun showExerciseInfo() {
        binding.videoGroup.isVisible = false
        binding.toolbarLayout.infoBtn.isVisible = false
        binding.infoGroup.isVisible = true
        binding.toolbarLayout.videoBtn.isVisible = true
        isShowInfo = true
    }

    //функция запуска
    private fun startProgressBar(time: Long) {
        binding.exercisePlayButton.speed = 2f
        currTimer = createTimer(binding.progressBar, time)
        currTimer?.let {
            it.start()
            binding.exercisePlayButton.playAnimation()
            animFlag = false
        }
    }

    //возвращение прогресс бара к изначальному состоянию
    private fun resetProgressBar() {
        currTimer?.let {
            it.cancel()
            binding.exercisePlayButton.speed = -2f
            binding.exercisePlayButton.playAnimation()
            binding.progressBar.progress = 0f
            animFlag = true
        }
    }

    //инициализация прогресс бара
    private fun initProgressBar() {
        binding.progressBar.progressBarColorStart = Color.parseColor("#00D2FF")
        binding.progressBar.progressBarColorEnd = Color.parseColor("#3A7BD5")
        binding.progressBar.backgroundProgressBarColorDirection =
            CircularProgressBar.GradientDirection.RIGHT_TO_LEFT
    }

    //запуск таймера
    private fun createTimer(progressBar: CircularProgressBar, time: Long): CountDownTimer {
        return object : CountDownTimer(time, time / 100) {
            override fun onTick(millisUntilFinished: Long) {
                progressBar.progress += 1f
            }

            override fun onFinish() {
                progressBar.progress = 100f
            }
        }
    }
}


