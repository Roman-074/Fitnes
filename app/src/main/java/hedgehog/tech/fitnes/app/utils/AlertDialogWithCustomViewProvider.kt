package hedgehog.tech.fitnes.app.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.NavInflater
import androidx.navigation.fragment.findNavController
import hedgehog.tech.fitnes.databinding.DialogFragmentConfirmExitExerciseInfoBinding
import java.util.zip.Inflater

object AlertDialogWithCustomViewProvider {
    fun provideAlertDialogWithCustomView(
        context: Context,
        navController: NavController,
        inflater: LayoutInflater
    ): AlertDialog {
        val dialog = AlertDialog.Builder(context)
            .create()
        val dialogBinding = DialogFragmentConfirmExitExerciseInfoBinding.inflate(inflater)
        dialog.apply {
            setView(dialogBinding.root)
            setCanceledOnTouchOutside(false)
        }
        dialogBinding.apply {
            exitDialogBtn.setOnClickListener {
                dialog.dismiss()
            }
            proceedBtn.setOnClickListener {
                navController.popBackStack()
                dialog.dismiss()
            }
        }
        return dialog
    }
}