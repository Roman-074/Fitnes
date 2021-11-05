package hedgehog.tech.fitnes.ui.main.workouts.exerciseInfo
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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

import java.util.*


@AndroidEntryPoint
class ExerciseInfoFragment : BaseFragment() {

    private var _binding: FragmentExerciseInfoBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: ExerciseInfoViewModel by viewModels()
    private val args: ExerciseInfoFragmentArgs by navArgs()

    //флаг показа анимации
    private var animFlag = true


    //таймер упражнения
    private var currTimer: CountDownTimer? = null

    //список упражнений
    private lateinit var exerciseSubs: List<SubWorkoutExerciseModel>
    private var currExerciseIndex = 0

    //флаг показа подробностей упражнения
    private var isShowInfo = false

    //история подтренировки
    private lateinit var historyModelSub: SubWorkoutHistoryModel

    //время начала ренировки
    private lateinit var startTime: Calendar

    private lateinit var workoutModel: WorkoutModel
    private lateinit var subWorkoutModel: SubWorkoutModel

    //Время выполнения каждого упражнения для подсчета калорий ключ -
    // порядковый номер упражнения, а значение - время выполнения
    private val exerciseTimes = mutableMapOf<Int, Long>()
    private var time = System.currentTimeMillis()

    private var animList: List<Int>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressBar()
        initButtons()
        initObservers()
        initOnBackPressed()
        initExerciseTime()
        viewmodel.getWorkoutById(args.workoutModelId)
        binding.exerciseDescription.movementMethod = ScrollingMovementMethod()
    }

    private fun initObservers() {
        //тренировка
        viewmodel.workout.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {1
                        workoutModel = it
                        viewmodel.getAllExercisesBySubWorkoutId(args.subWorkoutModelId)
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())
                }
            }
        })
        //упражнения подтренировки
        viewmodel.subWorkoutExercises.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        exerciseSubs = it
                        //Если упражнение разминка, пропускаем, так как уже размялись
                        if (args.isExistWarmUp) {
                            currExerciseIndex++
                        }
                        viewmodel.getSubworkoutById(args.subWorkoutModelId)
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())
                }
            }
        })
        //Загрузка модели подТренировки, для записи названия в историю
        viewmodel.subWorkout.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        subWorkoutModel = it
                        initHistory(it.id, it.title, workoutModel.title)
                        initExercise(false)
                    }
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())
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
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())

                }
            }
        })
        //Подсчет калорий и и передачи их в историю
        viewmodel.calories.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    historyModelSub.calories = result.data!!
                    viewmodel.insertSubWorkoutHistoryAndWorkoutHistory(
                        historyModelSub,
                        args.workoutModelId,
                        args.isChangeProgress,

                        )
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())
                }
            }
        })
        //переход на фрагмент с итогами тренировки
        viewmodel.isSuccessInsertSubWorkoutHistory.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    val time = Calendar.getInstance().timeInMillis - startTime.timeInMillis
                    findNavController().navigate(
                        ExerciseInfoFragmentDirections
                            .actionExerciseInfoFragmentToWorkoutOverFragment(
                                args.workoutModelId,
                                time,
                                args.subWorkoutModelId
                            )
                    )
                    viewmodel.isSuccessInsertSubWorkoutHistory.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(getString(R.string.error), requireView())

                }
            }
        })


    }

    // инициализируем время начала тренировки
    private fun initExerciseTime() {
        startTime = Calendar.getInstance()
    }

    // инциализируем историю подтренировк
    private fun initHistory(subWorkoutId: Int, subWorkoutTitle: String?, workoutTitle: String) {
        // массив с упражнение-статус выполнения
        val progress = exerciseSubs.map { Pair(it.queue ?: 0, false) }
        var title = workoutTitle
        subWorkoutTitle?.let { title += ". $it" }

        historyModelSub = SubWorkoutHistoryModel(
            subWorkoutId,
            title,
            progress.toMap().toMutableMap(),
            0,
            Calendar.getInstance().timeInMillis,
            0.0
        )
    }

    //действие при нажатии кнопки назад
    private fun initOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isShowInfo) {
                        showExerciseVideo()
                    } else {
//                        if (args.isExistWarmUp && currExerciseIndex == 1) {
//                            findNavController().popBackStack()
//                        } else if (currExerciseIndex == 0) {
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
        binding.exercisePlayButton.progress = 0f
        //время начала текущего упражнения
        time = System.currentTimeMillis()
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
            "${currExerciseIndex + 1}/${exerciseSubs.size}"
        binding.exerciseTitle.text = exerciseSubs.get(currExerciseIndex)?.title
        //если количество повторений нет, то не выставляем
        exerciseSubs[currExerciseIndex].repetitionsCount?.let {
            binding.exerciseRepetitions.text = getString(R.string.x, it)
            binding.exerciseCountInfo.text =
                resources.getQuantityString(R.plurals.repeat_num_times, it, it)
            binding.exerciseRepetitions.isVisible = true
        }
        //аналогично с подходами
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
        //и временем
        if (exerciseSubs[currExerciseIndex].time != null) {
            binding.approachesIv.isVisible = false
            binding.approachesTv.isVisible = false
            binding.exerciseRepetitions.isVisible = true
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = exerciseSubs[currExerciseIndex].time!!
            binding.exerciseRepetitions.text = DateParserUtil.parseDate(calendar, requireContext())
            binding.exerciseCountInfo.text = DateParserUtil.parseDate(calendar, requireContext())
        }
    }

    //завершаем тренировку
    private fun finishWorkout() {
        historyModelSub.workoutTime =
            Calendar.getInstance().timeInMillis - startTime.timeInMillis
        var count = 0
        //выставляем разминке статус выполнено
        if (args.isExistWarmUp) {
            historyModelSub.progress[1] = true
        }
        //считаем количество выполненных упражнений
        historyModelSub.progress.forEach {
            if (it.value) {
                count++
            }
        }

        //заносим историю и подсчитываем калории
        subWorkoutModel.completedCount = count
        viewmodel.updateSubWorkout(subWorkoutModel)
        viewmodel.calculateCalories(historyModelSub, exerciseTimes)


    }


    private fun initButtons() {
        binding.exercisePlayButton.setOnClickListener {
            if (animFlag) {
                    if (currExerciseIndex >= exerciseSubs.size){
                        finishWorkout()
                        return@setOnClickListener
                    }
                //если нет времени у упражнения, то при кнопке запуска, сразу переходить к следующему
                if (exerciseSubs[currExerciseIndex].time == null) {
                    historyModelSub.progress[currExerciseIndex + 1] = true
                    nextExercise()
                } else {
                    // запускаем прогрессбар
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
                historyModelSub.progress[currExerciseIndex + 1] = true
                nextExercise()
            }
        }
        //показываем подробную инфу упражнения
        binding.toolbarLayout.infoBtn.setOnClickListener {
            showExerciseInfo()
        }


        binding.toolbarLayout.videoBtn.setOnClickListener {
            showExerciseVideo()
        }
        binding.toolbarLayout.backBtn.setOnClickListener {
            // при нажатии кнопки назад если выбраны подробности упражнения, возвращаемся на окно упражнения
            if (isShowInfo) {
                showExerciseVideo()
            } else {
                //если мы не на первом упражнении, возвращаемся на предыдущее
                if (args.isExistWarmUp && currExerciseIndex == 1) {
                    findNavController().popBackStack()
                }
                if (currExerciseIndex == 0) {
                    findNavController().popBackStack()
                } else {
                    resetProgressBar()
                    previousExercise()
                }
            }

        }
    }

    // переход на следующее упражнение
    private fun nextExercise() {
        //заносим в словарь время выполнения упражнения
        exerciseTimes[currExerciseIndex + 1] = System.currentTimeMillis() - time
        currExerciseIndex++
        initExercise(false)
    }

    // переход на предыдущее
    private fun previousExercise() {
        currExerciseIndex--
        initExercise(false)
    }

    //показываем основной экран приложения с анимацией
    private fun showExerciseVideo() {
        binding.videoGroup.isVisible = true
        binding.toolbarLayout.infoBtn.isVisible = true
        binding.infoGroup.isVisible = false
        binding.toolbarLayout.videoBtn.isVisible = false
        isShowInfo = false
        initExercise(true)
        if (isPausedAnim && exerciseSubs[currExerciseIndex].time != null) {
            resumeProgressBar()
        }
    }

    //показываем описание упражнения
    private fun showExerciseInfo() {
        binding.videoGroup.isVisible = false
        binding.toolbarLayout.infoBtn.isVisible = false
        binding.infoGroup.isVisible = true
        binding.toolbarLayout.videoBtn.isVisible = true
        isShowInfo = true
        if (exerciseSubs[currExerciseIndex].time != null) {
            pauseProgressBar()
        }
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

    private var currentMillis: Long = 0
    private var isPausedAnim: Boolean = false

    //запуск таймера
    private fun createTimer(progressBar: CircularProgressBar, time: Long): CountDownTimer {
        return object : CountDownTimer(time, time / 100) {
            override fun onTick(millisUntilFinished: Long) {
                currentMillis = millisUntilFinished
                progressBar.progress += 1f
            }

            override fun onFinish() {
                progressBar.progress = 100f
            }
        }
    }

    private fun pauseProgressBar() {
        binding.exercisePlayButton.speed = -2f
        isPausedAnim = true
        currTimer?.cancel()
        binding.exercisePlayButton.playAnimation()
    }

    private fun resumeProgressBar() {
        if (currentMillis == 0L) return
        binding.exercisePlayButton.speed = 2f
        isPausedAnim = false
        currTimer = createTimer(binding.progressBar, currentMillis).start()
        binding.exercisePlayButton.playAnimation()
    }


}





