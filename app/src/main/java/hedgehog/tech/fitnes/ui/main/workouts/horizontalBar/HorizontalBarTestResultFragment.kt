package hedgehog.tech.fitnes.ui.main.workouts.horizontalBar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentHorizontalBarStartTestBinding
import hedgehog.tech.fitnes.databinding.FragmentHorizontalBarTestResultBinding
import hedgehog.tech.fitnes.databinding.FragmentMorningGymnasticBinding
import hedgehog.tech.fitnes.databinding.ToolbarHorizontalBarLayoutBinding
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HorizontalBarTestResultFragment : BaseFragment() {

    private var _binding: FragmentHorizontalBarTestResultBinding? = null
    private val binding get() = _binding!!

    private val args: HorizontalBarTestResultFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHorizontalBarTestResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbarLayout)
        initUI()
        initButtons()
    }

    private fun initUI() {
        // показываем разные вьюхи взависимости от сложности подтренировки
        when (args.subWorkoutType) {
            SubWorkoutModel.WEAK -> {
                binding.title.text = getString(R.string.level_beginner_test)
                binding.activityWeakImage.visibility = View.VISIBLE
                binding.activityAdvancedImage.visibility = View.INVISIBLE
                binding.activityMasterImage.visibility = View.INVISIBLE
            }
            SubWorkoutModel.ADVANCED -> {
                binding.title.text = getString(R.string.level_advanced_test)
                binding.activityWeakImage.visibility = View.INVISIBLE
                binding.activityAdvancedImage.visibility = View.VISIBLE
                binding.activityMasterImage.visibility = View.INVISIBLE
            }
            SubWorkoutModel.MASTER -> {
                binding.title.text = getString(R.string.level_master_test)
                binding.activityWeakImage.visibility = View.INVISIBLE
                binding.activityAdvancedImage.visibility = View.INVISIBLE
                binding.activityMasterImage.visibility = View.VISIBLE
            }
        }
    }

    private fun initButtons() {
        binding.proceedBtn.setOnClickListener {
            findNavController().navigate(
                HorizontalBarTestResultFragmentDirections
                    .actionHorizontalBarTestResultFragmentToHorizontalBarFragment(
                        args.workoutModelId,
                        args.subWorkoutModelId,
                        args.subWorkoutType
                    )
            )
        }
    }

    private fun initToolbar(toolbarLayout: ToolbarHorizontalBarLayoutBinding) {
        toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}