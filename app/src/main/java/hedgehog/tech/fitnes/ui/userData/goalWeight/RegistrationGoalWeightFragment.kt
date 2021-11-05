package hedgehog.tech.fitnes.ui.userData.goalWeight

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.ConverterUtil
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.databinding.FragmentRegistrationGoalWeightBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity

import timber.log.Timber

@AndroidEntryPoint
class RegistrationGoalWeightFragment : BaseFragment() {

    private var _binding: FragmentRegistrationGoalWeightBinding? = null
    private val binding get() = _binding!!
    private var isKg = true
    private val viewModel: RegistrationGoalWeightViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationGoalWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initEditText()
        initObservers()
        initToolbar()
    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = true
    }

    override fun onResume() {
        super.onResume()
        if (isKg){
            binding.kgWeightEditText.text = binding.kgWeightEditText.text
        }else{
            binding.lbWeightEditText.text = binding.lbWeightEditText.text
            binding.convertLbBtn.performClick()
        }
        bringKeyboard(binding.kgWeightEditText)
    }


    private fun initObservers() {
        viewModel.isSuccessSetGoalWeight.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    navigateToNextFragment()
                    viewModel.isSuccessSetGoalWeight.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }
    private fun navigateToNextFragment() {
        findNavController().navigate(
            R.id.action_registrationGoalWeightFragment_to_registrationPhotoFragment
        )
    }

    private fun initButtons() {
        (requireActivity() as OnToolbarButtonPressed).onBackButtonPressed {
            findNavController().popBackStack()
        }

        // переопределение действий при нажатии на тулбар
        (requireActivity() as OnToolbarButtonPressed).onSkipButtonPressed {
            navigateToNextFragment()
        }
            //кнопки изменения мер
        binding.convertKgBtn.setOnClickListener {
            binding.convertLbBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_gray_rounded_4
            )
            binding.convertKgBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_blue_rounded_4
            )
            binding.convertLbBtn.isEnabled = true
            binding.convertKgBtn.isEnabled = false
            binding.kgContainer.isVisible = true
            binding.lbContainer.isVisible = false
            isKg = true
            binding.lbWeightEditText.text?.clear()
        }
        binding.convertLbBtn.setOnClickListener {
            binding.convertKgBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_gray_rounded_4
            )
            binding.convertLbBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_blue_rounded_4
            )
            binding.convertKgBtn.isEnabled = true
            binding.convertLbBtn.isEnabled = false
            binding.kgContainer.isVisible = false
            binding.lbContainer.isVisible = true
            isKg = false
            binding.kgWeightEditText.text?.clear()
        }

        binding.proceedBtn.setOnClickListener {
            if (isKg){
                viewModel.setGoalWeight(binding.kgWeightEditText.text.toString().toFloat())
            } else{
                val tallInFt = binding.lbWeightEditText.text.toString().toFloat()
                viewModel.setGoalWeight(ConverterUtil.convertLbInKg(tallInFt))
            }
        }
    }

    //поведение кнопки при вводе текста
    private fun initEditText() {
        binding.kgWeightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.isNullOrEmpty() -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = false
                    }
                    (s.toString().toInt() > 400) or (s.toString().toInt() < 20) -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = true
                        binding.error.text = getString(R.string.please_enter_a_valid_height)
                    }

                    else -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_blue_rounded
                        )
                        binding.proceedBtn.isEnabled = true
                        binding.error.isVisible = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.lbWeightEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.isNullOrEmpty() -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = false
                    }
                    (s.toString().toFloat() < 66) or (s.toString().toFloat() > 440) -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = true
                        binding.error.text = getString(R.string.please_enter_a_valid_height)
                    }

                    else -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_blue_rounded
                        )
                        binding.proceedBtn.isEnabled = true
                        binding.error.isVisible = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }

    //выдвигание клавы при переходе на фрагмент

    private fun bringKeyboard(view: EditText?): Boolean {
        if (view == null) {
            return false
        }
        try {
            val value = view.text.toString()
            if (value.isEmpty()) {
                val imm: InputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                view.requestFocus()
                return true
            }
        } catch (e: Exception) {
            Timber.d(e)
        }
        return false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}