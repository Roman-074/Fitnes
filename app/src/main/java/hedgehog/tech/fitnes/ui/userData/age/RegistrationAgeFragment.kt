package hedgehog.tech.fitnes.ui.userData.age

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
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.databinding.FragmentRegistrationAgeBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import timber.log.Timber


@AndroidEntryPoint
class RegistrationAgeFragment : BaseFragment() {

    private var _binding: FragmentRegistrationAgeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationAgeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationAgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        initButtons()
        initObservers()
        initToolbar()
    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = !viewModel.isFirstLaunch().data!!
    }

    private fun initObservers() {
        viewModel.isSuccessSetAge.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    navigateToNextFragment()
                    viewModel.isSuccessSetAge.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }

    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationAgeFragmentDirections
                .actionRegistrationAgeFragmentToRegistrationTallFragment()
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
        binding.proceedBtn.setOnClickListener {
            viewModel.setAge(binding.ageEditText.text.toString().toInt())
        }
    }

    //поведение кнопки при вводе текста
    private fun initEditText() {
        binding.ageEditText.addTextChangedListener(object : TextWatcher {
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
                        binding.error.isVisible = false
                    }
                    s.toString().toInt() > 120 -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = true
                        binding.error.text = getString(R.string.you_must_be_at_most_120_years_old)

                    }
                    s.toString().toInt() < 13 -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.proceedBtn.isEnabled = false
                        binding.error.isVisible = true
                        binding.error.text = getString(R.string.you_must_be_at_least_13_years_old)
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

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        bringKeyboard(binding.ageEditText)
    }


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