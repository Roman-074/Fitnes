package hedgehog.tech.fitnes.ui.userData.registrationComplete

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.databinding.FragmentRegistrationCompleteBinding
import hedgehog.tech.fitnes.ui.main.MainActivity
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity


@AndroidEntryPoint
class RegistrationCompleteFragment : BaseFragment() {

    private var _binding: FragmentRegistrationCompleteBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: RegistrationCompleteViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
        hideToolbar()
        viewmodel.setFirstLaunch(false)
    }


    //прячу туллбар
    private fun hideToolbar() {
        (requireActivity() as RegistrationActivity).supportActionBar?.hide()
        (requireActivity() as RegistrationActivity).binding.toolbarLayout.isVisible = false
        //закрытие приложения при нажатии кнопки назад
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }


    private fun initButtons() {
        binding.startBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}