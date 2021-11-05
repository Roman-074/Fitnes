package hedgehog.tech.fitnes.ui.userData.tall

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
import hedgehog.tech.fitnes.databinding.FragmentRegistrationTallBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import timber.log.Timber

@AndroidEntryPoint
class RegistrationTallFragment : BaseFragment() {

    private var _binding: FragmentRegistrationTallBinding? = null
    private val binding get() = _binding!!
    private var isCm = true
    private val viewModel: RegistrationTallViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationTallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initObservers()
        initToolbar()

    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = !viewModel.isFirstLaunch().data!!
    }

    override fun onResume() {
        super.onResume()
        initEditText()
        if ( !binding.cmTallEditText.text.isNullOrEmpty()){
            binding.cmTallEditText.text = binding.cmTallEditText.text
        }
        if ( !binding.ftTallEditText.text.isNullOrEmpty()){
            binding.convertFtBtn.performClick()
            binding.ftTallEditText.text = binding.ftTallEditText.text
            binding.inTallEditText.text = binding.inTallEditText.text
        }
        bringKeyboard(binding.cmTallEditText)
    }





    private fun initObservers() {
        viewModel.isSuccessSetTall.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    navigateToNextFragment()
                    viewModel.isSuccessSetTall.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })


    }

    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationTallFragmentDirections
                .actionRegistrationTallFragmentToRegistrationCurrWeightFragment())
    }

    private fun initButtons() {
        (requireActivity() as OnToolbarButtonPressed).onBackButtonPressed {
            findNavController().popBackStack()
        }

        // переопределение действий при нажатии на тулбар
        (requireActivity() as OnToolbarButtonPressed).onSkipButtonPressed {
            navigateToNextFragment()
        }
        binding.convertSmBtn.setOnClickListener {
            binding.convertFtBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_gray_rounded_4
            )
            binding.convertSmBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_blue_rounded_4
            )
            binding.convertFtBtn.isEnabled = true
            binding.convertSmBtn.isEnabled = false
            binding.cmContainer.isVisible = true
            binding.ftContainer.isVisible = false
            isCm = true
            binding.ftTallEditText.text?.clear()
            binding.inTallEditText.text?.clear()

            binding.cmTallEditText.requestFocus()

        }
        binding.convertFtBtn.setOnClickListener {
            binding.convertSmBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_gray_rounded_4
            )
            binding.convertFtBtn.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.button_background_gradient_blue_rounded_4
            )
            binding.convertSmBtn.isEnabled = true
            binding.convertFtBtn.isEnabled = false
            binding.cmContainer.isVisible = false
            binding.ftContainer.isVisible = true
            isCm = false
            binding.cmTallEditText.text?.clear()

            binding.ftTallEditText.requestFocus()
        }
        //занесение роста в сантиметрах
        binding.proceedBtn.setOnClickListener {
            if (isCm) {
                viewModel.setTall(binding.cmTallEditText.text.toString().toFloat())
            } else {
                val tallInFt = binding.ftTallEditText.text.toString().toFloat()
                val tallInIn = binding.inTallEditText.text.toString().toFloat()
                viewModel.setTall(ConverterUtil.convertFtAndInToCm(tallInFt, tallInIn))
            }
        }
    }


    //поведение кнопки при вводе текста
    private fun initEditText() {
        binding.cmTallEditText.addTextChangedListener(initAddTextChangedListener(40,285))
        binding.ftTallEditText.addTextChangedListener(initAddTextChangedListener(3,8))
        binding.inTallEditText.addTextChangedListener(initAddTextChangedListener(0,11))
    }


    private fun initAddTextChangedListener(min:Int,max:Int): TextWatcher {
        return object : TextWatcher {

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
                    (s.toString().toInt() < min) or (s.toString().toInt() > max) -> {
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
        }
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