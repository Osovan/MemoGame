package com.osovan.memogame.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.osovan.memogame.databinding.DialogBinding

class Dialog(
     private val title: String,
     private val message: String,
     private val positiveButton: String,
     private val onSubmitClickListener: () -> Unit,
     private val negativeButton: String = "",
     private val onDismissClickListener: () -> Unit = {},
     private val isEndDialog: Boolean = false
) : DialogFragment() {
     private lateinit var binding: DialogBinding
     private var isNeutral = false

     override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
          binding = DialogBinding.inflate(layoutInflater)
          val builder = AlertDialog.Builder(requireActivity())
          builder.setView(binding.root)

          if (isEndDialog) {
               isCancelable = false
          }

          checkButtons()
          setTexts()
          setButtonsClick()

          val dialog = builder.create()
          dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
          return dialog
     }

     private fun checkButtons() {

          if ((positiveButton.isEmpty() || negativeButton.isEmpty()) &&
               !(positiveButton.isEmpty() && negativeButton.isEmpty())
          ) {
               isNeutral = true
               binding.llBothButtons.visibility = View.GONE
               binding.btnNeutralButton.visibility = View.VISIBLE
          }
     }

     private fun setTexts() {
          if (title.isNotEmpty()) {
               binding.tvTitle.text = title
          }
          if (message.isNotEmpty()) {
               binding.tvMessage.text = message
          }
          if (isNeutral) {
               if (positiveButton.isNotEmpty()) {
                    binding.btnNeutralButton.text = positiveButton
               }
          } else {
               if (positiveButton.isNotEmpty()) {
                    binding.btnPositiveButton.text = positiveButton
               }
               if (negativeButton.isNotEmpty()) {
                    binding.btnNegativeButton.text = negativeButton
               }
          }
     }

     private fun setButtonsClick() {

          if (isNeutral) {
               binding.btnNeutralButton.setOnClickListener {
                    onSubmitClickListener.invoke()
                    dismiss()
               }
          } else {
               binding.btnPositiveButton.setOnClickListener {
                    onSubmitClickListener.invoke()
                    dismiss()
               }
               binding.btnNegativeButton.setOnClickListener {
                    onDismissClickListener.invoke()
                    dismiss()
               }
          }
     }
}