package com.drivequant.drivekit.common.ui.utils

import android.content.Context
import android.content.DialogInterface
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView
import android.view.WindowManager
import com.drivequant.drivekit.common.ui.R

object DKAlertDialog {

    data class LayoutBuilder(
        private var alertDialog: AlertDialog,
        private var builder: AlertDialog.Builder,
        private var layout: LinearLayout? = null,
        private var context: Context? = null) {

        fun init(context: Context) = apply {
            this.context = context
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.layout_alert_dialog,null)
            builder = AlertDialog.Builder(context)
            layout = view.findViewById(R.id.items_wrapper)
            builder.setView(view)
            alertDialog = builder.create()
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

        fun message(message: String) = apply { alertDialog.setMessage(message) }

        fun negativeButton(negativeText: String, negativeListener: DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText, negativeListener)
        }

        fun positiveButton(positiveText: String, positiveListener: DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveText, positiveListener)
        }

        fun neutralButton(neutralText: String, neutralListener: DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, neutralText, neutralListener)
        }

        fun cancelable(cancelable: Boolean) = apply { alertDialog.setCancelable(cancelable) }

        fun layout(layoutId: Int) = apply {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(layoutId, null)
            layout?.addView(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        fun image(imageResId: Int) = apply {
            val imageView = ImageView(context)
            imageView.setImageDrawable(ContextCompat.getDrawable(context!!, imageResId))
            layout?.addView(
                imageView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun show(): AlertDialog {
            alertDialog.show()
            return alertDialog
        }

        fun dismiss() {
            alertDialog.dismiss()
        }
    }

    data class AlertBuilder(
        private var alertDialog: AlertDialog,
        private var title: String,
        private var message: String,
        private var positiveText: String,
        private var negativeText: String) {

        fun init(context: Context) = apply {
            alertDialog = AlertDialog.Builder(context, R.style.ProgressDialogStyle).create()
        }

        fun title(title: String) = apply {
            alertDialog.setTitle(title)
        }

        fun message(message: String) = apply { alertDialog.setMessage(message) }

        fun positiveButton(positiveText: String, positiveListener: DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveText, positiveListener)
        }

        fun negativeButton(negativeText: String,negativeListener:DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, negativeText, negativeListener)
        }

        fun neutralButton(neutralText: String, neutralListener: DialogInterface.OnClickListener) = apply {
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, neutralText, neutralListener)
        }

        fun iconResId(iconResId: Int) = apply {
            alertDialog.setIcon(iconResId)
        }

        fun cancelable(cancelable: Boolean) = apply { alertDialog.setCancelable(cancelable) }

        @JvmOverloads
        fun show(
            height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
            width: Int = WindowManager.LayoutParams.MATCH_PARENT) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(alertDialog.window?.attributes)
            layoutParams.width = width
            layoutParams.height = height
            alertDialog.window?.attributes = layoutParams
            alertDialog.show()
        }

        fun dismiss() {
            alertDialog.dismiss()
        }
    }
}