package hedgehog.tech.fitnes.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.databinding.RecycleItemProfileHistoryDateBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileAdapterHistory(
) :
    RecyclerView.Adapter<ProfileAdapterHistory.ViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<List<SubWorkoutHistoryModel>>() {
        override fun areItemsTheSame(oldItem: List<SubWorkoutHistoryModel>, newItem: List<SubWorkoutHistoryModel>): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: List<SubWorkoutHistoryModel>, newItem: List<SubWorkoutHistoryModel>): Boolean {
            return oldItem == newItem
        }
    }


    private val differ = AsyncListDiffer(this, differCallback)

    var subWorkoutHistoryModel: List<List<SubWorkoutHistoryModel>>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    inner class ViewHolder(val binding: RecycleItemProfileHistoryDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: List<SubWorkoutHistoryModel>) {
            binding.month.text = parseMonth(item.first().workoutDate)
            //создаем в списке месяцев тренировок список с тренировками в этот список
            val adapterExercise = ProfileAdapterExercise()
            adapterExercise.subWorkoutHistoryModels = item
            binding.recycleView.apply {
                adapter = adapterExercise
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
    //показывает месяц тренировок
    private fun parseMonth(workoutDate: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = workoutDate
        return SimpleDateFormat("LLLL",Locale.getDefault()).format(calendar.time) + " ${calendar.get(Calendar.YEAR)}"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemProfileHistoryDateBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subWorkoutHistoryModel[position])
    }

    override fun getItemCount(): Int {
        return subWorkoutHistoryModel.size
    }
}