package hedgehog.tech.fitnes.ui.main.workouts.horizontalBar.horizontalBarTestAproaches

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.EventObserver
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.data.models.SubWorkoutModel
import hedgehog.tech.fitnes.databinding.*
import kotlinx.android.synthetic.main.fragment_horizontal_bar_test_approaches_count.*
import timber.log.Timber


@AndroidEntryPoint
class HorizontalBarTestAproachesFragment :
    BaseFragment() {

    private var _binding: FragmentHorizontalBarTestApproachesCountBinding? = null
    private val binding get() = _binding!!

    private val args: HorizontalBarTestAproachesFragmentArgs by navArgs()
    private val viewmodel: HorizontalBarTestAproachesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentHorizontalBarTestApproachesCountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        initButtons()
        initObservers()
    }

    private fun initObservers() {
        viewmodel.subworkouts.observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Success ->{
                    findNavController().navigate(
                        HorizontalBarTestAproachesFragmentDirections
                            .actionHorizontalBarTestAproachesFragmentToHorizontalBarTestResultFragment(
                                args.workoutModelId,
                                result.data!![0].id,
                                result.data[0].hardType
                            )
                    )
                    viewmodel.subworkouts.value = null
                }
                is Resource.Error ->{
                    snackbar(getString(R.string.error))
                }
        }
    })
    }

    private fun initButtons() {

        binding.toolbarLayout.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.proceedBtn.setOnClickListener {
            //выставляем сложность взависимости от количества введеных подтягиваний
            when (binding.pullsCountEditText.text.toString().toInt()) {
                in 0..3 -> {
                    viewmodel.getSubWorkoutsByWorkoutId(args.workoutModelId, SubWorkoutModel.WEAK)
                }
                in 3..10 -> {
                    viewmodel.getSubWorkoutsByWorkoutId(
                        args.workoutModelId,
                        SubWorkoutModel.ADVANCED
                    )

                }
                else -> {
                    viewmodel.getSubWorkoutsByWorkoutId(args.workoutModelId, SubWorkoutModel.MASTER)
                }
            }
        }
    }

    //поведение кнопки при вводе текста
    private fun initEditText() {
        binding.pullsCountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.isNullOrEmpty() -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                    }
                    else -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_blue_rounded
                        )
                        binding.proceedBtn.isEnabled = true
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


}