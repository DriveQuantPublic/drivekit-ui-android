package com.drivekit.demoapp.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.notification.settings.activity.NotificationSettingsActivity
import com.drivekit.demoapp.settings.enum.UserInfoType
import com.drivekit.demoapp.settings.viewmodel.SettingsViewModel
import com.drivekit.demoapp.utils.restartApplication
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_settings.*

internal class SettingsActivity: AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.parameters_header)

        initConfiguration()
    }

    override fun onResume() {
        super.onResume()
        checkViewModelInitialization()
    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        }
    }

    private fun initConfiguration() {
        checkViewModelInitialization()
        initUserInfoSection()
        initAutostartSection()
        initNotificationSection()
        initLogoutSection()
        initDeleteAccountSection()
        listOf(
            view_separator_1,
            view_separator_2,
            view_separator_3,
            view_separator_4,
            view_separator_5,
            view_separator_6,
            view_separator_7
        ).forEach {
            it.setBackgroundColor(DriveKitUI.colors.neutralColor())
        }
        viewModel.updateUserInfoLiveData.observe(this) { success ->
            if (success) {
                initUserInfoSection()
            } else {
                Toast.makeText(this, getString(R.string.client_error), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.logoutLiveData.observe(this) {
            restartApplication()
        }
    }

    private fun initUserInfoSection() {
        initTitle(title_account, R.string.parameters_account_title, R.drawable.ic_account)
        description_account.normalText(DriveKitUI.colors.complementaryFontColor())

        label_user_id.headLine2()
        label_firstname.headLine2()
        label_lastname.headLine2()
        label_pseudo.headLine2()

        text_user_id.text = viewModel.getUserId()
        text_user_id.normalText(DriveKitUI.colors.complementaryFontColor())

        viewModel.getUserInfo(object : GetUserInfoQueryListener {
            override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                initUserInfoData(UserInfoType.FIRST_NAME, text_firstname, userInfo?.firstname)
                initUserInfoData(UserInfoType.LAST_NAME, text_lastname, userInfo?.lastname)
                initUserInfoData(UserInfoType.PSEUDO, text_pseudo, userInfo?.pseudo)
            }
        })
    }

    private fun initAutostartSection() {
        initTitle(title_autostart, R.string.parameters_auto_start_title, R.drawable.ic_autostart)
        manageAutoStartDescription(viewModel.isAutoStartEnabled(this))
        switch_autostart.apply {
            isChecked = viewModel.isAutoStartEnabled(this@SettingsActivity)
            setOnClickListener {
                viewModel.activateAutoStart(this@SettingsActivity, isChecked)
                manageAutoStartDescription(isChecked)
            }
        }
    }

    private fun manageAutoStartDescription(isEnabled: Boolean) {
        description_autostart.apply {
            if (isEnabled) {
                text = getString(R.string.parameters_auto_start_enabled)
                normalText(DriveKitUI.colors.complementaryFontColor())
            } else {
                text = getString(R.string.parameters_auto_start_disabled)
                normalText(DriveKitUI.colors.warningColor())
            }
        }
    }

    private fun initNotificationSection() {
        initTitle(title_notifications, R.string.parameters_notification_title, R.drawable.ic_notifications)
        description_notifications.normalText(DriveKitUI.colors.complementaryFontColor())
        button_notifications.headLine2(DriveKitUI.colors.secondaryColor())
        button_notifications.setOnClickListener {
            NotificationSettingsActivity.launchActivity(this)
        }
    }

    private fun initLogoutSection() {
        button_logout_account.apply {
            normalText(DriveKitUI.colors.criticalColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
            setOnClickListener {
                manageLogoutClick()
            }
        }
    }

    private fun initDeleteAccountSection() {
        initTitle(title_account_deletion, R.string.parameters_delete_account_title, R.drawable.dk_trash)
        button_delete_account.apply {
            normalText(DriveKitUI.colors.criticalColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
            setOnClickListener {
                manageDeleteAccount()
            }
        }
        api_info_account_deletion.normalText(DriveKitUI.colors.warningColor())
    }

    private fun manageDeleteAccount() {
        DeleteAccountActivity.launchActivity(this)
    }

    private fun initTitle(view: TextView, titleResId: Int, iconResId: Int) {
        val accountIcon = ContextCompat.getDrawable(this, iconResId)
        view.apply {
            text = getString(titleResId)
            headLine1()
            compoundDrawablePadding = 18
            val bitmap = (accountIcon as BitmapDrawable).bitmap
            val size = context.resources.getDimension(R.dimen.dk_ic_big).toInt()
            val resizedDrawable: Drawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, size, size, true))
            setCompoundDrawablesWithIntrinsicBounds(resizedDrawable, null, null, null)
        }
    }

    private fun initUserInfoData(type: UserInfoType, view: TextView, data: String?) {
        view.apply {
            text = if (data.isNullOrBlank()) {
                normalText(DriveKitUI.colors.warningColor())
                when (type) {
                    UserInfoType.FIRST_NAME -> R.string.parameters_enter_firstname
                    UserInfoType.LAST_NAME -> R.string.parameters_enter_lastname
                    UserInfoType.PSEUDO -> R.string.parameters_enter_pseudo
                }.let {
                    getString(it)
                }
            } else {
                normalText(DriveKitUI.colors.secondaryColor())
                data
            }
            setOnClickListener {
                manageEditUserInfo(type, data)
            }
        }
    }

    private fun manageEditUserInfo(type: UserInfoType, data: String?) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dk_alert_dialog_edit_value, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog = builder.create()
        val titleTextView = view.findViewById<TextView>(R.id.text_view_title)
        val editText = view.findViewById<TextInputEditText>(R.id.edit_text_field)
        titleTextView.apply {
            text = when (type) {
                UserInfoType.FIRST_NAME -> R.string.parameters_enter_firstname
                UserInfoType.LAST_NAME -> R.string.parameters_enter_lastname
                UserInfoType.PSEUDO -> R.string.parameters_enter_pseudo
            }.let {
                getString(it)
            }
            normalText(DriveKitUI.colors.fontColorOnSecondaryColor())
            setBackgroundColor(DriveKitUI.colors.primaryColor())
        }
        editText.apply {
            inputType = InputType.TYPE_CLASS_TEXT
            setText(data)
            requestFocus()
        }
        alertDialog.apply {
            setCancelable(true)
            setButton(
                DialogInterface.BUTTON_POSITIVE, getString(R.string.dk_common_validate)) { dialog, _ ->
                viewModel.updateUserInfo(type, editText.text.toString())
                dialog.dismiss()
            }
            setButton(
                DialogInterface.BUTTON_NEGATIVE, getString(R.string.dk_common_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
            show()
        }
    }

    private fun manageLogoutClick() {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this)
            .layout(R.layout.template_alert_dialog_layout)
            .positiveButton(getString(R.string.dk_common_confirm)) { _, _ ->
                viewModel.logout(this@SettingsActivity)
            }
            .negativeButton(getString(R.string.dk_common_cancel))
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = getString(R.string.app_name)
        descriptionTextView?.text = getString(R.string.logout_confirmation)

        titleTextView?.headLine1()
        descriptionTextView?.normalText()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
