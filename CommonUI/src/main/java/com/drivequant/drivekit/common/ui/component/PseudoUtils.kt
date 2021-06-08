package com.drivequant.drivekit.common.ui.component

import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.drivequant.drivekit.common.ui.R
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKResource
import com.drivequant.drivekit.core.DriveKit
import com.drivequant.drivekit.core.SynchronizationType
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UpdateUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.google.android.material.textfield.TextInputEditText

object PseudoUtils {

    fun checkPseudo(listener: PseudoCheckListener) {
        DriveKit.getUserInfo(object : GetUserInfoQueryListener {
            override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                if (userInfo.hasPseudo()) {
                    listener.onPseudoChecked(true)
                } else {
                    DriveKit.getUserInfo(object : GetUserInfoQueryListener {
                        override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) =
                            when (status) {
                                UserInfoGetStatus.SUCCESS,
                                UserInfoGetStatus.CACHE_DATA_ONLY -> {
                                    listener.onPseudoChecked(userInfo.hasPseudo())
                                }
                                UserInfoGetStatus.FAILED_TO_SYNC_USER_INFO_CACHE_ONLY -> {
                                    listener.onPseudoChecked(false)
                                }
                            }
                    }, SynchronizationType.DEFAULT)
                }
            }
        }, SynchronizationType.CACHE)
    }

    fun show(context: Context, listener: PseudoChangedListener) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dk_alert_dialog_pseudo, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        val alertDialog = builder.create()
        val editText = view.findViewById<TextInputEditText>(R.id.edit_text_pseudo)
        val titleTextView = view.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = view.findViewById<TextView>(R.id.text_view_alert_description)

        alertDialog.apply {
            setButton(DialogInterface.BUTTON_POSITIVE,
                DKResource.convertToString(context, "dk_common_validate")
            ) { _, _ ->
                // Do nothing, onClick() callback is overriden in getButton()
            }
            setButton(DialogInterface.BUTTON_NEGATIVE,
                DKResource.convertToString(context, "dk_common_cancel")
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                listener.onCancelled()
            }
            setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
            show()
        }

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val newPseudo = editText.editableText.toString()
            if (newPseudo.isNotBlank()) {
                it.isEnabled = false
                DriveKit.updateUserInfo(
                    pseudo = newPseudo,
                    listener = object : UpdateUserInfoQueryListener {
                        override fun onResponse(status: Boolean) {
                            it.isEnabled = true
                            if (status) {
                                alertDialog.dismiss()
                            }
                            listener.onPseudoChanged(status)
                        }
                    }
                )
            } else {
                Toast.makeText(context, DKResource.convertToString(context, "dk_fields_not_valid"), Toast.LENGTH_LONG).show()
            }
        }

        titleTextView.apply {
            text = DKResource.convertToString(context, "app_name")
            headLine1()
        }
        descriptionTextView.apply {
            text = DKResource.convertToString(context, "dk_common_no_pseudo_set")
            normalText()
        }
    }

    private fun UserInfo?.hasPseudo(): Boolean =
        (this?.pseudo != null && this.pseudo!!.isNotBlank())
}

interface PseudoCheckListener {
    fun onPseudoChecked(hasPseudo: Boolean)
}

interface PseudoChangedListener {
    fun onPseudoChanged(success: Boolean)
    fun onCancelled()
}