package hedgehog.tech.fitnes.ui.main.workouts.sevenDaysTrainingCourse

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.RecycleItemSevenDaysTrainingCourseSubWorkoutBinding
import kotlin.math.roundToInt

class SevenDaysTrainingCourseAdapter(
) :
    RecyclerView.Adapter<SevenDaysTrainingCourseAdapter.ViewHolder>() {

    lateinit var context: Context

    private val differCallback = object : DiffUtil.ItemCallback<SubWorkoutModel>() {
        override fun areItemsTheSame(oldItem: SubWorkoutModel, newItem: SubWorkoutModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SubWorkoutModel,
            newItem: SubWorkoutModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var progress = 0

    //номер текущей тренировки, чтобы поменять фон элемента
    fun setProgress(newProgress: Int) {
        progress = newProgress
        notifyDataSetChanged()
    }

    private var onSubWorkoutClickListener: ((SubWorkoutModel,Int) -> Unit)? = null

    fun setOnSubWorkoutClickListener(i: ((SubWorkoutModel,Int) -> Unit)) {
        onSubWorkoutClickListener = i
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var subWorkouts: List<SubWorkoutModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewHolder(val binding: RecycleItemSevenDaysTrainingCourseSubWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubWorkoutModel, position: Int) {
            //выставление цвета элемента в соответствии с активной подтренировкой
            if (position == progress) {
                binding.itemContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.layout_background_gradient_blue)
                binding.subWorkoutTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.progressText.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.progressBar.progressBarColor =
                    ContextCompat.getColor(context, R.color.white)
                binding.restIcon.setImageResource(R.drawable.seven_days_training_course_rv_item_img_scr_cup_current)
            } else {
                binding.itemContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.recycle_item_exercise_background)
                binding.subWorkoutTitle.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.progressText.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.restIcon.setImageResource(R.drawable.seven_days_training_course_rv_item_img_src_cup)
                initProgressBar(binding)
            }
            binding.subWorkoutTitle.text =
                context.getString(R.string.day_num, position + 1, (item.title))

            //изменение прогресс бара на иконку в дни отдыха
            if (item.title == "Rest day") {
                binding.restIcon.isVisible = true
                binding.progressGroup.isVisible = false
            } else {
                binding.progressGroup.isVisible = true
                binding.restIcon.isVisible = false
                //выставления процента выполнения тренировки на основе количества заданий
                binding.progressBar.progress =
                    (100.0 / item.exerciseCount!! * item.completedCount!!).roundToInt().toFloat()
                binding.progressText.text =
                    (100.0 / item.exerciseCount!! * item.completedCount!!).roundToInt()
                        .toString() + "%"
            }

            binding.itemContainer.setOnClickListener {

                onSubWorkoutClickListener?.let { click ->
                    click(item,position)
                }
            }
        }
    }

    //окраска прогрессбара в градиент
    private fun initProgressBar(binding: RecycleItemSevenDaysTrainingCourseSubWorkoutBinding) {
        binding.progressBar.progressBarColorStart = Color.parseColor("#00D2FF")
        binding.progressBar.progressBarColorEnd = Color.parseColor("#3A7BD5")
        binding.progressBar.backgroundProgressBarColorDirection =
            CircularProgressBar.GradientDirection.RIGHT_TO_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding =
            RecycleItemSevenDaysTrainingCourseSubWorkoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subWorkouts[position], position)
    }

    override fun getItemCount(): Int {
        return subWorkouts.size
    }
}