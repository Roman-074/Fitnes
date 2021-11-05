package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.DaysOfWeek
import hedgehog.tech.fitnes.app.utils.EventObserver
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.databinding.DialogFragmentReminderBinding
import hedgehog.tech.fitnes.ui.main.workouts.workoutOver.WorkoutOverFragmentArgs
import java.util.*


@AndroidEntryPoint
class ReminderDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentReminderBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: ReminderDialogViewModel by viewModels()
    private val args: ReminderDialogFragmentArgs by navArgs()


    private val checkedList = mutableListOf<DaysOfWeek?>(null,null,null,null,null,null,null)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initObservers()
    }

    private fun initObservers() {
        viewmodel.isSuccessInsert.observe(viewLifecycleOwner,EventObserver{
            val navController = findNavController()
                //передаем фрагменту команду перезагрузить список
            navController.previousBackStackEntry?.savedStateHandle?.set(Constants.REMINDER_DIALOG_BACK_KEY, true)
            navController.popBackStack()
        })
    }

    private fun initButtons() {
        val curDateTime = Calendar.getInstance()
        binding.cancel.setOnClickListener {
            findNavController().popBackStack()

        }
            //выставляем дни повторений
        binding.ok.setOnClickListener {
            checkedList[0] =  if (binding.mon.isChecked ) DaysOfWeek.MONDAY else null
            checkedList[1] =  if (binding.tue.isChecked ) DaysOfWeek.TUESDAY else null
            checkedList[2] =  if (binding.wed.isChecked ) DaysOfWeek.WEDNESDAY else null
            checkedList[3] =  if (binding.thu.isChecked ) DaysOfWeek.THURSDAY else null
            checkedList[4] =  if (binding.fri.isChecked ) DaysOfWeek.FRIDAY else null
            checkedList[5] =  if (binding.sat.isChecked ) DaysOfWeek.SATURDAY else null
            checkedList[6] =  if (binding.sun.isChecked ) DaysOfWeek.SUNDAY else null
            //вызываем виджет с выбором времени
            val callback = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                pickedDateTime.set(Calendar.MINUTE,minute)
                viewmodel.insertReminder(ReminderModel(args.workoutId,pickedDateTime.timeInMillis, checkedList.filterNotNull() ,false))
            }
            TimePickerDialog(requireContext(),callback,curDateTime.get(Calendar.HOUR_OF_DAY),curDateTime.get(Calendar.MINUTE),false).show()
        }

    }
}