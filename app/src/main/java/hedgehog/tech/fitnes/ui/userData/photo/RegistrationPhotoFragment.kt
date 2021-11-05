package hedgehog.tech.fitnes.ui.userData.photo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import hedgehog.tech.fitnes.R
import hedgehog.tech.fitnes.app.utils.BaseFragment
import hedgehog.tech.fitnes.app.utils.Constants
import hedgehog.tech.fitnes.app.utils.Resource
import hedgehog.tech.fitnes.app.utils.SnackbarUtils
import hedgehog.tech.fitnes.databinding.FragmentRegistrationPhotoBinding
import hedgehog.tech.fitnes.ui.userData.OnToolbarButtonPressed
import hedgehog.tech.fitnes.ui.userData.RegistrationActivity
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RegistrationPhotoFragment : BaseFragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRegistrationPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: RegistrationPhotoViewModel by viewModels()
    @Inject
    lateinit var glide: RequestManager

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.reg_photo_rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.reg_photo_rotate_close_anim) }
    private val fromTop: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.reg_photo_from_top_anim) }
    private val toTop: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.reg_photo_to_top_anim) }

    private var clicked = false

    var filePath: Uri? = null

    private lateinit var cropLauncher: ActivityResultLauncher<Any?>




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        //Контракт для запуска активити с обрезанием
        val cropActivityResultContract = object : ActivityResultContract<Any?,Uri?>(){
            override fun createIntent(context: Context, input: Any?): Intent {
                return CropImage.activity()
                    .setAspectRatio(4,4)
                    .getIntent(requireActivity())
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return CropImage.getActivityResult(intent)?.uriContent
            }
        }
        cropLauncher = registerForActivityResult(cropActivityResultContract){ result ->
            result?.let {
                Timber.d("start viewmodel functions")
                glide.load(it).into(binding.profilePhoto)
                viewmodel.setPathToPhoto(it.toString())
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()


        initToolbar()
        hideKeyboard(view)
        requestReadStoragePermissionAndInitObservers()
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initToolbar() {
        (requireActivity() as RegistrationActivity).binding.skipBtn.isVisible = true
    }

    private fun initObservers() {
        viewmodel.isStandartPhoto.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    result.data?.let { isStandart ->
                        if (isStandart){
                            viewmodel.getPhotoIndex()
                        } else{
                            viewmodel.getPathToPhoto()
                        }
                    }
                    viewmodel.isStandartPhoto.value = null
                }
                is Resource.Error ->{
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
        viewmodel.photoIndex.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    result.data?.let { photoIndex ->
                        binding.profilePhoto.setImageResource(Constants.STANDART_PHOTOS[photoIndex])
                    } ?: run{
                        binding.profilePhoto.setImageResource(R.drawable.reg_photo_img_src_person)
                    }
                }
                is Resource.Error ->{
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })

        viewmodel.photoPath.observe(viewLifecycleOwner,{result ->
            when(result){
                is Resource.Success ->{
                    result.data?.let { photoPath ->
                        Timber.d("load photo in view")
                        val uri = Uri.parse(photoPath)
                        glide.load(uri).into(binding.profilePhoto)
                        viewmodel.photoPath.value = null
                    }
                }
                is Resource.Error ->{
                    SnackbarUtils.showShortSnackbar(this.resources.getString(R.string.error),requireView())
                }
            }
        })
    }

    private fun initButtons() {


        (requireActivity() as OnToolbarButtonPressed).onBackButtonPressed {
            findNavController().popBackStack()
        }

        // переопределение действий при нажатии на тулбар
        (requireActivity() as OnToolbarButtonPressed).onSkipButtonPressed {
            findNavController().navigate(
                RegistrationPhotoFragmentDirections
                    .actionRegistrationPhotoFragmentToRegistrationActivityLevelFragment())
        }
        binding.fabMenu.setOnClickListener { requestReadStoragePermissionAndInitObservers()
            onAddButtonClicked()
        }
        binding.fabSelect.setOnClickListener {
            findNavController().navigate(
                RegistrationPhotoFragmentDirections
                    .actionRegistrationPhotoFragmentToRegistrationPhotoStandart()
            )
        }
        binding.fabFromGallery.setOnClickListener {
            cropLauncher.launch(null)
        }
        binding.proceedBtn.setOnClickListener{
            findNavController().navigate(
                RegistrationPhotoFragmentDirections
                    .actionRegistrationPhotoFragmentToRegistrationActivityLevelFragment())
        }
    }


    //действие при нажатии на кнопку
    private fun onAddButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    override fun onResume() {
        super.onResume()
        viewmodel.isStandartPhoto()
    }

    //используем анимации
    private fun setAnimation(clicked: Boolean) {
        if (!clicked){
            binding.fabSelect.startAnimation(fromTop)
            binding.fabFromGallery.startAnimation(fromTop)
            binding.fabMenu.startAnimation(rotateOpen)

            val alphaAnim = AlphaAnimation(0.0f,1.0f)

            binding.photoFabTitle.animate().alpha(1f).duration = 800
            binding.standartFabTitle.animate().alpha(1f).duration = 800
        } else{
            binding.fabSelect.startAnimation(toTop)
            binding.fabFromGallery.startAnimation(toTop)
            binding.fabMenu.startAnimation(rotateClose)

            binding.photoFabTitle.animate().alpha(0f).duration = 500
            binding.standartFabTitle.animate().alpha(0f).duration = 500


        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked){
            binding.fabSelect.isVisible = true
            binding.fabFromGallery.isVisible = true
            binding.fabSelect.isClickable = true
            binding.fabFromGallery.isClickable = true
        } else{
            binding.fabSelect.visibility = View.INVISIBLE
            binding.fabFromGallery.visibility = View.INVISIBLE
            binding.fabSelect.isClickable = false
            binding.fabFromGallery.isClickable = false

        }
    }

    //Запрос ращрешений на запись и хранение файлов
    private fun requestReadStoragePermissionAndInitObservers() {
        if (EasyPermissions.hasPermissions(
                requireContext(),
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
            )
        ) {
            initObservers()
            viewmodel.isStandartPhoto()
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.the_app_will_not_work_correctly_without_permissions),
            0,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    }

    //делегирование разрешений библиотеке EasyPermission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            initObservers()
            viewmodel.isStandartPhoto()
        } else {
            requestReadStoragePermissionAndInitObservers()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        snackbar("The app can't work properly, please go to settings and give the necessary permissions")
    }


}