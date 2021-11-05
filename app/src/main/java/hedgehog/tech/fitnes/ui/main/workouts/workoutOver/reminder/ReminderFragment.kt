package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.*
import hedgehog.tech.fitnes.databinding.FragmentReminderBinding
import javax.inject.Inject

@AndroidEntryPoint
class ReminderFragment : BaseFragment() {


    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: ReminderViewModel by viewModels()
    private val args: ReminderFragmentArgs by navArgs()

    private lateinit var reminderAdapter: ReminderAdapter

    @Inject
    lateinit var alarmManager: AlarmManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapters(binding.recycleView)
        initObservers()
        initButtons()
        viewmodel.getRemindersByWorkoutId(args.workoutId)

    }

    //сервис запускающий все будильники
    private fun startReminders() {
        requireContext().startService(
            Intent(
                requireContext(),
                ReminderLaunchAlarmsService::class.java
            )
        )
    }


    private fun initButtons() {
        binding.fab.setOnClickListener {
            findNavController().navigate(
                ReminderFragmentDirections.actionReminderFragmentToReminderDialogFragment(args.workoutId)
            )
        }
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers() {
        viewmodel.reminders.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let {
                        reminderAdapter.reminders = it
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

        viewmodel.isSuccessChangeEnabledStatus.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    startReminders()
                    viewmodel.isSuccessChangeEnabledStatus.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })

        //отслеживание удаления, для обновления alarm сервисов
        viewmodel.isSuccessDelete.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    startReminders()
                    viewmodel.isSuccessDelete.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(
                        this.resources.getString(R.string.error),
                        requireView()
                    )
                }
            }
        })
        // отслеживание бэкстэка после возвращения с диалогового окна для создания напоминалки
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constants.REMINDER_DIALOG_BACK_KEY)
            ?.observe(viewLifecycleOwner) {
                if (it) {
                    viewmodel.getRemindersByWorkoutId(args.workoutId)
                }
            }
    }

    private fun initAdapters(recycleView: RecyclerView) {

        reminderAdapter = ReminderAdapter(requireContext())
        reminderAdapter.onChangeRimenderClickListener {
            when (it.isEnable) {
                true -> {
                    viewmodel.changeEnableStatusOfReminderById(it.id, false)
                    it.isEnable = false
                }
                false -> {
                    viewmodel.changeEnableStatusOfReminderById(it.id, true)
                    it.isEnable = true
                }
            }
        }
        reminderAdapter.onRemoveRimenderClickListener {
            viewmodel.deleteReminder(requireContext(), it, args.workoutId, alarmManager)
        }
        recycleView.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onStop() {
        super.onStop()
        startReminders()
    }


}