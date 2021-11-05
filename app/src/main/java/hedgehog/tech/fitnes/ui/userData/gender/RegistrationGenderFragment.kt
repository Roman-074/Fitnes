package hedgehog.tech.fitnes.ui.userData.gender

import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.databinding.FragmentRegistrationGenderBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import timber.log.Timber

@AndroidEntryPoint
class RegistrationGenderFragment : BaseFragment() {

    private var _binding: FragmentRegistrationGenderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationGenderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioGroup()
        initObservers()
        initButtons()
        initToolbar()
    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = !viewModel.isFirstLaunch().data!!
    }


    private fun initObservers() {
        viewModel.isSuccessSetGender.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    navigateToNextFragment()
                    viewModel.isSuccessSetGender.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }


    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationGenderFragmentDirections
                .actionRegistrationGenderFragmentToRegistrationNameFragment()
        )
    }


    private fun initButtons() {
        binding.proceedBtn.setOnClickListener {
            if (binding.radioMale.isChecked) {
                viewModel.setGender("male")
            } else {
                viewModel.setGender("female")
            }
        }

        (requireActivity() as OnToolbarButtonPressed).onBackButtonPressed {
            requireActivity().finish()
        }

        // переопределение действий при нажатии на тулбар
        (requireActivity() as OnToolbarButtonPressed).onSkipButtonPressed {
            navigateToNextFragment()
        }

    }


    //функция для реализации переключения фотографии пола, и цвета радио-кнопки
    private fun initRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioMale.id -> setActiveRadioMale()
                binding.radioFemale.id -> setActiveRadioFemale()
            }
        }
    }

    private fun setActiveRadioFemale() {
        binding.genderMaleImage.visibility = View.INVISIBLE
        binding.genderFemaleImage.visibility = View.VISIBLE
        binding.radioFemale.setButtonColor(R.color.text_blue)
        binding.radioMale.setButtonColor(R.color.black)
    }

    private fun setActiveRadioMale() {
        binding.genderMaleImage.visibility = View.VISIBLE
        binding.genderFemaleImage.visibility = View.INVISIBLE
        binding.radioFemale.setButtonColor(R.color.black)
        binding.radioMale.setButtonColor(R.color.text_blue)
    }

    private fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(
            requireContext(),
            colorId
        )
    }

    private fun RadioButton.setButtonColor(color: Int) {
        setTextColor(getColor(color))
        setCircleColor(getColor(color))
    }

    //функция из интернета для смены цвета макркера радио-кнопки
    private fun RadioButton.setCircleColor(color: Int) {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                Color.BLACK,
                color
            )
        )
        buttonTintList = colorStateList
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            buttonTintBlendMode = BlendMode.SRC_IN
        } else {
            buttonTintMode = PorterDuff.Mode.SRC_IN
        }
        invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}