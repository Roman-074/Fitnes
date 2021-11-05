package hedgehog.tech.fitnes.app.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import hedgehog.tech.fitnes.R
import timber.log.Timber
import java.lang.StringBuilder
import java.util.*

object DialogUtil {
    fun createConfirmDialog(
        context: Context,
        clickListener: DialogInterface.OnClickListener
    ): AlertDialog.Builder =
        AlertDialog.Builder(context).run {
            setTitle("")
            setMessage("")
            setPositiveButton(android.R.string.yes, clickListener)
            setNegativeButton(android.R.string.no, null)
            setIcon(android.R.drawable.ic_dialog_alert)
        }

}