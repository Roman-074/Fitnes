package hedgehog.tech.fitnes.ui.userData.activityLevel

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
import hedgehog.tech.fitnes.databinding.FragmentRegistrationActivityLevelBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity

@AndroidEntryPoint
class RegistrationActivityLevelFragment :
    BaseFragment() {

    private var _binding: FragmentRegistrationActivityLevelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationActivityLevelViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationActivityLevelBinding.inflate(inflater, container, false)
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
        viewModel.isSuccessSetActivityLevel.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    navigateToNextFragment()
                    viewModel.isSuccessSetActivityLevel.value = null
                }
                is Resource.Error -> {
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })

    }


    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationActivityLevelFragmentDirections
                .actionRegistrationActivityLevelFragmentToRegistrationCompleteFragment()
        )
    }


    private fun initButtons() {
        //выставление навыков пользователя на основе выбранной radio кнопки
        binding.proceedBtn.setOnClickListener {
            when {
                binding.radioWeak.isChecked -> {
                    viewModel.setActivityLevel("weak")
                }
                binding.radioAdvanced.isChecked -> {
                    viewModel.setActivityLevel("advanced")
                }
                else -> {
                    viewModel.setActivityLevel("master")
                }
            }
        }

        (requireActivity() as OnToolbarButtonPressed).onBackButtonPressed {
            findNavController().popBackStack()
        }

        // переопределение действий при нажатии на тулбар
        (requireActivity() as OnToolbarButtonPressed).onSkipButtonPressed {
            navigateToNextFragment()
        }

    }


    //функция для реализации переключения фотографии  и цвета радио-кнопки
    private fun initRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioWeak.id -> setActiveRadioWeak()
                binding.radioAdvanced.id -> setActiveRadioAdvanced()
                binding.radioMaster.id -> setActiveRadioMaster()
            }
        }
    }

    private fun setActiveRadioMaster() {
        binding.activityMasterImage.visibility = View.VISIBLE
        binding.activityWeakImage.visibility = View.INVISIBLE
        binding.activityAdvancedImage.visibility = View.INVISIBLE

        binding.radioMaster.setButtonColor(R.color.text_blue)
        binding.radioWeak.setButtonColor(R.color.black)
        binding.radioAdvanced.setButtonColor(R.color.black)
    }


    private fun setActiveRadioAdvanced() {
        binding.activityAdvancedImage.visibility = View.VISIBLE
        binding.activityWeakImage.visibility = View.INVISIBLE
        binding.activityMasterImage.visibility = View.INVISIBLE

        binding.radioAdvanced.setButtonColor(R.color.text_blue)
        binding.radioWeak.setButtonColor(R.color.black)
        binding.radioMaster.setButtonColor(R.color.black)
    }

    private fun setActiveRadioWeak() {
        binding.activityWeakImage.visibility = View.VISIBLE
        binding.activityAdvancedImage.visibility = View.INVISIBLE
        binding.activityMasterImage.visibility = View.INVISIBLE

        binding.radioWeak.setButtonColor(R.color.text_blue)
        binding.radioMaster.setButtonColor(R.color.black)
        binding.radioAdvanced.setButtonColor(R.color.black)
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