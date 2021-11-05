package hedgehog.tech.fitnes.ui.main.workouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.RecycleItemWorkoutsBinding

class WorkoutsAdapter(
) :
    RecyclerView.Adapter<WorkoutsAdapter.ViewHolder>() {


    private val differCallback = object : DiffUtil.ItemCallback<WorkoutModel>() {
        override fun areItemsTheSame(oldItem: WorkoutModel, newItem: WorkoutModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkoutModel, newItem: WorkoutModel): Boolean {
            return oldItem == newItem
        }
    }

    private var onWorkoutClickListener: ((WorkoutModel) -> Unit)? = null

    fun setOnWorkoutClickListener (i: ((WorkoutModel) -> Unit)){
        onWorkoutClickListener = i
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var workouts: List<WorkoutModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewHolder(val binding: RecycleItemWorkoutsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkoutModel) {
            binding.workoutTitle.text = item.title
            binding.time.text = item.time
                //переопределяем кликлистенер на элемент
            binding.workoutItem.setOnClickListener {
                onWorkoutClickListener?.let { click ->
                    click(item)
                    
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemWorkoutsBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(workouts[position])
    }

    override fun getItemCount(): Int {
        return workouts.size
    }
}