package hedgehog.tech.fitnes.ui.main.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.databinding.FragmentProfileBinding
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private lateinit var historyAdapter: ProfileAdapterHistory


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDiary()
        initButtons()
        initObservers()
        initAdapters()
        viewModel.getUser()
        viewModel.getAllSubWorkoutsHistorySortedByDate()
    }



    private fun initAdapters() {
        historyAdapter = ProfileAdapterHistory()
        binding.recycleView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    //выставляем дни недели в дневник
    private fun initDiary() {
        //список дней с пн по вс
        val dateList = mutableListOf<Int>()

        val calendar = Calendar.getInstance()
        //ищем пн
        while (calendar[Calendar.DAY_OF_WEEK] != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1)
        }
        //добавляем пн
        dateList.add(calendar.get(Calendar.DAY_OF_MONTH))
        //добавляем всё по вс
        while (calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1)
            dateList.add(calendar.get(Calendar.DAY_OF_MONTH))
        }
        setDates(dateList)
    }

    //выставляем числа в профиле и обводим в кольцо текущую
    private fun setDates(dateList: MutableList<Int>) {
        binding.monDate.text = dateList[0].toString()
        binding.tueDate.text = dateList[1].toString()
        binding.wedDate.text = dateList[2].toString()
        binding.thuDate.text = dateList[3].toString()
        binding.friDate.text = dateList[4].toString()
        binding.satDate.text = dateList[5].toString()
        binding.sunDate.text = dateList[6].toString()
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) -1) {
            Calendar.MONDAY -> binding.monDate.background = getCurrDateBackground()
            Calendar.TUESDAY -> binding.tueDate.background = getCurrDateBackground()
            Calendar.WEDNESDAY -> binding.wedDate.background = getCurrDateBackground()
            Calendar.THURSDAY -> binding.thuDate.background = getCurrDateBackground()
            Calendar.FRIDAY -> binding.friDate.background = getCurrDateBackground()
            Calendar.SATURDAY -> binding.satDate.background = getCurrDateBackground()
            Calendar.SUNDAY -> binding.sunDate.background = getCurrDateBackground()
        }
    }

    private fun getCurrDateBackground() = (ContextCompat.getDrawable(
        requireContext(),
        R.drawable.profile_current_date_background_elipse
    ))

    private fun initObservers() {
        viewModel.user.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> initUI(result.data)
                is Resource.Error -> snackbar(getString(R.string.error))
            }
        })

        viewModel.history.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        historyAdapter.subWorkoutHistoryModel = it
                        if (it.isNotEmpty()){
                            initWorkoutCard(it.first().first())
                            binding.history.isVisible = false
                        }

                    }

                }
                is Resource.Error -> snackbar(getString(R.string.error))
            }
        })

    }

    //выставляем данные последней тренировки в cardView
    private fun initWorkoutCard(first: SubWorkoutHistoryModel) {
        val locale: Locale? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else{
            resources.configuration.locale
        }
        val simpleDateFormat = SimpleDateFormat("mm:ss",locale)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = first.workoutTime
        binding.timeText.text = simpleDateFormat.format(Date(first.workoutTime))
        binding.caloriesText.text = String.format("%.2f",(first.calories / 1000))
        initScore(first.progress)
    }

    //выставляем очки за упражнения последней тренировки
    private fun initScore(progress: MutableMap<Int, Boolean>) {
        var count = 0
        progress.forEach {
            if (it.value){
                count++
            }
        }
        binding.scoreText.text = count.toString()
    }

    private fun initUI(data: UserModel?) {
        data?.let { user ->
            user.name?.let {
                binding.nameTitle.isVisible = false
                binding.name.text = user.name
                binding.name.typeface =
                    ResourcesCompat.getFont(requireContext(), R.font.righteous_regular)
            }
            //показываем данные пользователя в профиле
            user.age?.let {
                binding.oldTitle.isVisible = false
                binding.old.text =  resources.getQuantityString (
                    R.plurals.years_old,
                    it,
                    it
                )
            }

            user.tall?.let {
                binding.tall.text =  getString(R.string.num_cm, it.roundToInt().toString())
            }

            user.currWeight?.let {
                binding.currWeight.text = getString(R.string.num_kg, it.roundToInt().toString())
            }

            user.goalWeight?.let {
                binding.goalWeight.text = getString(R.string.num_kg, it.roundToInt().toString())
            }

            user.activityLevel?.let {
                binding.level.text = it.lowercase(Locale.getDefault())
            }
            //выставляем фото профиля
            user.isStandartPhoto?.let {
                if (it) {
                    user.photoIndex?.let {index ->
                        glide.load(Constants.STANDART_PHOTOS[index])
                            .into(binding.avatar)
                    }
                } else {
                    glide.load(user.pathToPhoto).into(binding.avatar)
                }
            }
        }
    }


    private fun initButtons() {
        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), RegistrationActivity::class.java))
        }
        binding.toolbarLayout.feedbackBtn.setOnClickListener {
            ToastUtils.showShortToast("Тэм, эта кнопка пока не работает(")
        }
    }

}