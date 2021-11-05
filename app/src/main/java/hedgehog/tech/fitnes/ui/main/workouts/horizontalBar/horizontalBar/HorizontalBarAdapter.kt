package hedgehog.tech.fitnes.ui.main.workouts.horizontalBar.horizontalBar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.DateParserUtil
import hedgehog.tech.fitnes.data.models.SubWorkoutExerciseModel
import hedgehog.tech.fitnes.databinding.RecycleItemExerciseBinding
import java.util.*

class HorizontalBarAdapter(
) :
    RecyclerView.Adapter<HorizontalBarAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<SubWorkoutExerciseModel>() {
        override fun areItemsTheSame(oldItem: SubWorkoutExerciseModel, newItem: SubWorkoutExerciseModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubWorkoutExerciseModel, newItem: SubWorkoutExerciseModel): Boolean {
            return oldItem == newItem
        }
    }



    private val differ = AsyncListDiffer(this, differCallback)

    var subWorkoutExercises: List<SubWorkoutExerciseModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewHolder(val binding: RecycleItemExerciseBinding,val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubWorkoutExerciseModel) {
            //выставляем нужную информацию об упражнении
            binding.exerciseTitle.text = "${item.queue}. ${item.title}"
            item.repetitionsCount?.let {
                binding.repetitions.isVisible = true
                binding.repetitions.text = context.getString(R.string.x,it)
            }

            item.approachesCount?.let {
                binding.approachesTv.isVisible = true
                binding.approachesIv.isVisible = true
                binding.approachesTv.text = it.toString()
            }

            item.time?.let {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                binding.repetitions.isVisible = true
                binding.repetitions.text = DateParserUtil.parseDate(calendar,context)
                binding.approachesTv.isVisible = false
                binding.approachesIv.isVisible = false

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemExerciseBinding.inflate(inflater,parent,false)
        return ViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subWorkoutExercises[position])
    }

    override fun getItemCount(): Int {
        return subWorkoutExercises.size
    }
}