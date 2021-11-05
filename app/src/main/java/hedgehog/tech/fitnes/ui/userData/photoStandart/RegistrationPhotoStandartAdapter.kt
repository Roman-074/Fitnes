package hedgehog.tech.fitnes.ui.userData.photoStandart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.databinding.RecycleItemRegistrationPhotoStandartBinding

class RegistrationPhotoStandartAdapter(
    var images: List<Int>
) :
    RecyclerView.Adapter<RegistrationPhotoStandartAdapter.ViewHolder>() {
    private var onImageClickListener: ((Int) -> Unit)? = null
    private var activePosition: Int = 0
    //взять позицию выбранной фотографии
    fun getActivePosition(): Int{
        return activePosition
    }


    fun setOnImageClickListener(i: ((Int) -> Unit)) {
        onImageClickListener = i
    }
    //выборо другой фотографии
    fun setNewActiveImage(i: List<Int>,newPosition: Int) {
        images = i
        activePosition = newPosition
        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding: RecycleItemRegistrationPhotoStandartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int, position: Int) {
            binding.image.setImageResource(item)
            //есл неактивно наложить на фото тинт
            if (position != activePosition){
                binding.image.setColorFilter(
                    ContextCompat.getColor(
                        binding.image.context,
                        R.color.reg_photo_standart_tint
                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                )
            } else{
                binding.image.colorFilter = null
            }
            binding.image.setOnClickListener {
                onImageClickListener?.let { click ->
                    click(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecycleItemRegistrationPhotoStandartBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}