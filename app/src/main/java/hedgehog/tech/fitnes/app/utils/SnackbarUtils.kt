package hedgehog.tech.fitnes.app.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

//Класс для показа снэкбаров
object SnackbarUtils {
    private val allSnackbars: ArrayList<Snackbar> = arrayListOf()

    private fun killAllSnackbar(){
        allSnackbars.forEach{it.dismiss()}
        allSnackbars.clear()
    }

    fun showShortSnackbar(message: String,view:View){
        killAllSnackbar()
        val newSnackbar = Snackbar.make(view,message,Snackbar.LENGTH_SHORT)
        allSnackbars.add(newSnackbar)
        newSnackbar.show()
    }

    fun showLongSnackbar(message: String,view:View){
        killAllSnackbar()
        val newSnackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG)
        allSnackbars.add(newSnackbar)
        newSnackbar.show()
    }
}