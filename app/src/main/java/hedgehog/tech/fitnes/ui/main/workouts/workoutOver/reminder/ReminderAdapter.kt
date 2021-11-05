package hedgehog.tech.fitnes.ui.main.workouts.workoutOver.reminder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.app.utils.DaysOfWeek
import hedgehog.tech.fitnes.data.models.ReminderModel
import hedgehog.tech.fitnes.databinding.RecycleItemReminderBinding
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    val appContext: Context
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ReminderModel>() {
        override fun areItemsTheSame(oldItem: ReminderModel, newItem: ReminderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReminderModel, newItem: ReminderModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var reminders: List<ReminderModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onChangeRimenderClickListener: ((ReminderModel) -> Unit)? = null

    fun onChangeRimenderClickListener(i: ((ReminderModel) -> Unit)) {
        onChangeRimenderClickListener = i
    }

    private var onRemoveRimenderClickListener: ((ReminderModel) -> Unit)? = null

    fun onRemoveRimenderClickListener(i: ((ReminderModel) -> Unit)) {
        onRemoveRimenderClickListener = i
    }

    inner class ViewHolder(val binding: RecycleItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReminderModel) {
            val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            binding.time.text = simpleDateFormat.format(Date(item.time))
            binding.repeatDays.text = setRepeatDays(item.repetitions)
            binding.enableReminder.isChecked = item.isEnable
            binding.enableReminder.setOnClickListener {
                onChangeRimenderClickListener?.let { click ->
                    click(item)
                }
            }
            binding.removeReminder.setOnClickListener {
                onRemoveRimenderClickListener?.let { click ->
                    click(item)
                }
            }
        }
    }

    //выставляем дни по которым идет повторение
    private fun setRepeatDays(repetitions: List<DaysOfWeek>): String {
        val repetitionsString = StringBuilder()
        repetitions.forEach {
            repetitionsString.append("${appContext.resources.getString(it.shortName)}, ")
        }
        //убираем ", " у последнего элемента
        repetitionsString.delete(repetitionsString.length - 2, repetitionsString.length - 1)
        return repetitionsString.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemReminderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int {
        return reminders.size
    }
}