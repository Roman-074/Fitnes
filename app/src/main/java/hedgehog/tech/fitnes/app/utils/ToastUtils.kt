package hedgehog.tech.fitnes.app.utils

import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import hedgehog.tech.fitnes.app.BaseApp

//класс для показа тоастов
object ToastUtils {
    private val allToasts: ArrayList<Toast> = arrayListOf()

    private fun killAllToasts(){
        allToasts.forEach{it.cancel()}
        allToasts.clear()
    }

    fun showShortToast(message: String){
        killAllToasts()
        val newSnackbar = Toast.makeText(BaseApp.getInstance().applicationContext,message,Toast.LENGTH_SHORT)
        allToasts.add(newSnackbar)
        newSnackbar.show()
    }

    fun showLongToast(message: String){
        killAllToasts()
        val newSnackbar = Toast.makeText(BaseApp.getInstance().applicationContext,message,Toast.LENGTH_LONG)
        allToasts.add(newSnackbar)
        newSnackbar.show()
    }
}