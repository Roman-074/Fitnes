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
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.data.models.WorkoutModel
import hedgehog.tech.fitnes.databinding.FragmentHorizontalBarStartTestBinding
import hedgehog.tech.fitnes.databinding.FragmentMorningGymnasticBinding
import hedgehog.tech.fitnes.databinding.ToolbarHorizontalBarLayoutBinding
import javax.inject.Inject


@AndroidEntryPoint
class HorizontalBarStartTestFragment : BaseFragment() {

    private var _binding: FragmentHorizontalBarStartTestBinding? = null
    private val binding get() = _binding!!

    private val args: HorizontalBarStartTestFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHorizontalBarStartTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbarLayout)
        initButtons()
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initButtons() {
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                HorizontalBarStartTestFragmentDirections
                    .actionHorizontalBarStartTestFragmentToHorizontalBarTestDescriptionFragment(args.workoutModelId)
            )
        }
    }

    private fun initToolbar(toolbarLayout: ToolbarHorizontalBarLayoutBinding) {
        toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


}