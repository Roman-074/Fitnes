package hedgehog.tech.fitnes.ui.main.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.data.models.SubWorkoutHistoryModel
import hedgehog.tech.fitnes.databinding.RecycleItemProfileHistoryExercisesBinding
import java.text.SimpleDateFormat
import java.util.*

class ProfileAdapterExercise(
) :
    RecyclerView.Adapter<ProfileAdapterExercise.ViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<SubWorkoutHistoryModel>() {
        override fun areItemsTheSame(oldItem: SubWorkoutHistoryModel, newItem: SubWorkoutHistoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubWorkoutHistoryModel, newItem: SubWorkoutHistoryModel): Boolean {
            return oldItem == newItem
        }
    }


    private val differ = AsyncListDiffer(this, differCallback)

    var subWorkoutHistoryModels: List<SubWorkoutHistoryModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewHolder(val binding: RecycleItemProfileHistoryExercisesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubWorkoutHistoryModel) {
            //вытавляем подтренировки в историю
            binding.title.text = item.workoutTitle
            binding.exercisesCount.text = setCompletedExerciseCount(item.progress)
            binding.date.text = parseDate(item.workoutDate)
        }
    }

    private fun parseDate(workoutDate: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = workoutDate
        return SimpleDateFormat("LLLL",Locale.getDefault()).format(calendar.time) + " ${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    //выставляем количество выполненных упражнений
    private fun setCompletedExerciseCount(progress: MutableMap<Int, Boolean>): String {
        var count = 0
        progress.forEach{
            if (it.value){
                count++
            }
        }
        return count.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemProfileHistoryExercisesBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subWorkoutHistoryModels[position])
    }

    override fun getItemCount(): Int {
        return subWorkoutHistoryModels.size
    }
}