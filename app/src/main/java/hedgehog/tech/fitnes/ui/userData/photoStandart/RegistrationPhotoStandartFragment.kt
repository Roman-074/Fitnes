package hedgehog.tech.fitnes.ui.userData.photoStandart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.databinding.FragmentRegistrationPhotoStandartBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity

@AndroidEntryPoint
class RegistrationPhotoStandartFragment :
    BaseFragment() {

    private var _binding: FragmentRegistrationPhotoStandartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationPhotoStandartViewModel by viewModels()

    private var activePosition = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationPhotoStandartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        initAdapter()
        initObservers()
        initToolbar()
    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = true
    }

    private fun initObservers() {
        viewModel.isSuccessSetStandartPhoto.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    findNavController().popBackStack()
                    viewModel.isSuccessSetStandartPhoto.value = null
                }
                is Resource.Error ->{
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }


    //создание адаптера для списка стандартных фоток
    private fun initAdapter() {
        val flexLayout = FlexboxLayoutManager(requireContext()).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            alignItems = AlignItems.CENTER
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        val adapterRV = RegistrationPhotoStandartAdapter(Constants.STANDART_PHOTOS)
        //переопределение нажатия на элемент списка
        adapterRV.setOnImageClickListener {
            adapterRV.setNewActiveImage(Constants.STANDART_PHOTOS, it)
            activePosition = it
        }
        binding.photoRv.apply {
            layoutManager = flexLayout
            adapter = adapterRV
        }


    }


    private fun navigateToNextFragment() {
        findNavController().navigate(
            RegistrationPhotoStandartFragmentDirections
                .actionRegistrationPhotoStandartToRegistrationActivityLevelFragment())
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
            viewModel.setStandartPhoto(activePosition)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}