package hedgehog.tech.fitnes.ui.userData.name

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
import hedgehog.tech.fitnes.databinding.FragmentRegistrationNameBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import timber.log.Timber

@AndroidEntryPoint
class RegistrationNameFragment : BaseFragment() {

    private var _binding: FragmentRegistrationNameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationNameViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationNameBinding.inflate(inflater, container, false)
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

    //поведение кнопки при вводе текста
    private fun initEditText() {
        binding.nameEt.addTextChangedListener(object : TextWatcher {
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
                    s.length > 12 -> {
                        binding.proceedBtn.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.button_background_gradient_gray_rounded
                        )
                        binding.error.isVisible = true
                        binding.error.text = getString(R.string.the_name_is_too_big)
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

    private fun initObservers() {
        viewModel.isSuccessSetName.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                   navigateToNextFragment()
                    viewModel.isSuccessSetName.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }



    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationNameFragmentDirections
                .actionRegistrationNameFragmentToRegistrationAgeFragment())
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
            viewModel.setName(binding.nameEt.text.toString())
        }


    }

    override fun onResume() {
        super.onResume()
        bringKeyboard(binding.nameEt)
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