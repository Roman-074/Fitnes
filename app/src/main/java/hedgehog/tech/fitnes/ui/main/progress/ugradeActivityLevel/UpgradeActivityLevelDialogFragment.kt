package hedgehog.tech.fitnes.ui.main.progress.ugradeActivityLevel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.UserScoreUtil
import hedgehog.tech.fitnes.data.models.UserModel
import hedgehog.tech.fitnes.databinding.DialogFragmentUpgradeActivityLevelBinding

@AndroidEntryPoint
class UpgradeActivityLevelDialogFragment : DialogFragment() {


    private var _binding: DialogFragmentUpgradeActivityLevelBinding? = null
    private val binding get() = _binding!!

    private val viewmodel: UpgradeActivityLevelDialogViewModel by viewModels()

    private val args: UpgradeActivityLevelDialogFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentUpgradeActivityLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        //берем текущего юзера
        viewmodel.getUser()
    }
    private fun initObservers() {
        viewmodel.user.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    result.data?.let {
                        initUI(it)
                        initButtons(it)
                    }
                }
                is Resource.Error ->{
                    Snackbar.make(requireView(),getString(R.string.error), Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        viewmodel.isSuccessSetLevel.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    val navController = findNavController()
                    navController.previousBackStackEntry?.savedStateHandle?.set(Constants.PROGRESS_DIALOG_BACK_KEY, true)
                    navController.popBackStack()
                    viewmodel.isSuccessSetLevel.value = null
                }
                is Resource.Error ->{
                    Snackbar.make(requireView(),getString(R.string.error), Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initButtons(user: UserModel) {
        binding.selectCurrLevel.setOnClickListener {
            findNavController().popBackStack()
        }
            //выставляем текст в кнопки в зависимости от уровня навыков пользователя
        when(user.activityLevel){
            "weak" ->{
                binding.selectUpLevel.text = getString(R.string.select_advanced_level)
                binding.selectCurrLevel.text = getString(R.string.proceed_weak_level)
                binding.selectUpLevel.setOnClickListener {
                    viewmodel.setActivityLevel("advanced")
                }
            }
            "advanced" ->{
                binding.selectUpLevel.text = getString(R.string.select_master_level)
                binding.selectCurrLevel.text = getString(R.string.proceed_advanced_level)
                binding.selectUpLevel.setOnClickListener {
                    viewmodel.setActivityLevel("master")
                }
            }
        }
    }

    //выставляем полученные очки в вьюшку
    private fun initUI(user: UserModel) {
        when(user.activityLevel){
            "weak" ->{
                binding.points.text = getString(R.string.you_get_num_points,UserScoreUtil.setUserScore(args.exercisesCount,"weak"))
            }
            "advanced" ->{
                binding.points.text = getString(R.string.you_get_num_points,UserScoreUtil.setUserScore(args.exercisesCount,"advanced"))
            }
        }
    }
}