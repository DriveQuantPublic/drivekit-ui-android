package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.graphical.DKColors

object DKAlertDialog {

    data class LayoutBuilder(
        private var alertDialog: AlertDialog? = null,
        private var builder: AlertDialog.Builder? = null,
        private var layout: LinearLayout? = null) {

        private lateinit var context: Context

        fun init(context: Context) = apply {
            this.context = context
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.empty_alert_dialog_layout,null)
            builder = AlertDialog.Builder(context, R.style.DKAlertDialog)
            layout = view.findViewById(R.id.items_wrapper)
            builder?.setView(view)
            alertDialog = builder?.create()
        }

        fun title(title: String) = apply {
            val titleView = TextView(context)
            titleView.text = title
            titleView.gravity = Gravity.CENTER
            layout?.addView(
                titleView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun message(message: String) = apply { alertDialog?.setMessage(message) }

        fun negativeButton(
            negativeText: String = context.getString(R.string.dk_common_cancel),
            negativeListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
        ) = apply {
            alertDialog?.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText, negativeListener)
        }

        fun positiveButton(
            positiveText: String = context.getString(R.string.dk_common_ok),
            positiveListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() }
        ) = apply {
            alertDialog?.setButton(DialogInterface.BUTTON_POSITIVE, positiveText, positiveListener)
        }

        fun cancelable(cancelable: Boolean) = apply { alertDialog?.setCancelable(cancelable) }

        fun layout(layoutId: Int) = apply {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(layoutId, null)
            layout?.addView(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun image(imageResId: Int) = apply {
            val imageView = ImageView(context)
            imageView.setImageDrawable(ContextCompat.getDrawable(context, imageResId))
            layout?.addView(
                imageView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun show(): AlertDialog {
            alertDialog?.apply {
                show()
                listOf(
                    DialogInterface.BUTTON_POSITIVE,
                    DialogInterface.BUTTON_NEGATIVE,
                    DialogInterface.BUTTON_NEUTRAL
                ).forEach { buttonType ->
                    getButton(buttonType)?.apply {
                        setTypeface(DriveKitUI.primaryFont(context), typeface.style)
                        setTextColor(DKColors.secondaryColor)
                    }
                }
            }
            return alertDialog!!
        }

        fun dismiss() {
            alertDialog?.dismiss()
        }
    }
}
