package hedgehog.tech.fitnes.app.utils

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

// Класс для добавления методов ко всем фрагментам

open class BaseFragment : Fragment() {

    //показ снэкбаров
    fun snackbar(msg: String) = Snackbar.make(
        requireView() ,
        msg ,
        Snackbar.LENGTH_LONG
    ).show()
}