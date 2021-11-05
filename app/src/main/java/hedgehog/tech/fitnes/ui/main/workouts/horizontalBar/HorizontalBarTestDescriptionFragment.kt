package hedgehog.tech.fitnes.ui.main.workouts.horizontalBar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.databinding.FragmentHorizontalBarTestDescriptionBinding



@AndroidEntryPoint
class HorizontalBarTestDescriptionFragment : BaseFragment() {

    private var _binding: FragmentHorizontalBarTestDescriptionBinding? = null
    private val binding get() = _binding!!

    private val args: HorizontalBarStartTestFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHorizontalBarTestDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initButtons() {
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                HorizontalBarTestDescriptionFragmentDirections
                    .actionHorizontalBarTestDescriptionFragmentToHorizontalBarTestAproachesFragment(
                        args.workoutModelId
                    )
            )
        }
    }




}