package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.view.WindowManager
import com.drivequant.drivekit.common.ui.R


object DKAlertDialog {

    data class AlertBuilder(
        private var alertDialog: AlertDialog? = null,
        private var title: String? = null,
        private var message: String? = null,
        private var positiveText: String? = null,
        private var negativeText: String? = null
    ) {
        fun init(context: Context) = apply {
            alertDialog = AlertDialog.Builder(context, R.style.ProgressDialogStyle).create()
        }

        fun title(title: String) = apply {
            alertDialog?.setTitle(title)
        }

        fun message(message: String) = apply {
            alertDialog?.setMessage(message)
        }

        fun positiveButton(
            positiveText: String,
            positiveListener: DialogInterface.OnClickListener) = apply {
            alertDialog?.setButton(DialogInterface.BUTTON_POSITIVE, positiveText, positiveListener)
        }

        fun negativeButton(
            negativeText: String,
            negativeListener: DialogInterface.OnClickListener) = apply {
            alertDialog?.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText, negativeListener)
        }

        fun neutralButton(neutralText: String, neutralListener: DialogInterface.OnClickListener) = apply {
                alertDialog?.setButton(DialogInterface.BUTTON_NEUTRAL, neutralText, neutralListener)
            }

        fun iconResId(iconResId: Int) = apply {
            alertDialog?.setIcon(iconResId)
        }

        fun cancelable(cancelable: Boolean) = apply { alertDialog?.setCancelable(cancelable) }

        @JvmOverloads
        fun show(
            height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
            width: Int = WindowManager.LayoutParams.MATCH_PARENT) {
            alertDialog?.show()
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog?.window!!.attributes)
            layoutParams.width = width
            layoutParams.height = height
            alertDialog?.window!!.attributes = layoutParams
        }

        fun dismiss() {
            alertDialog?.dismiss()
        }
    }
}