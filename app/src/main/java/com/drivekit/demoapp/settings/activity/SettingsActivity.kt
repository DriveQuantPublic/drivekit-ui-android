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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.notification.settings.activity.NotificationSettingsActivity
import com.drivekit.demoapp.settings.enum.UserInfoType
import com.drivekit.demoapp.settings.viewmodel.SettingsViewModel
import com.drivekit.demoapp.utils.restartApplication
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivitySettingsBinding
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
import com.google.android.material.textfield.TextInputEditText

internal class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.dkToolbar)
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
            binding.viewSeparator1,
            binding.viewSeparator2,
            binding.viewSeparator3,
            binding.viewSeparator4,
            binding.viewSeparator5,
            binding.viewSeparator6,
            binding.viewSeparator7
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
        initTitle(binding.titleAccount, R.string.parameters_account_title, R.drawable.ic_account)
        binding.descriptionAccount.normalText(DriveKitUI.colors.complementaryFontColor())

        binding.labelUserId.headLine2()
        binding.labelFirstname.headLine2()
        binding.labelLastname.headLine2()
        binding.labelPseudo.headLine2()

        binding.textUserId.text = viewModel.getUserId()
        binding.textUserId.normalText(DriveKitUI.colors.complementaryFontColor())

        viewModel.getUserInfo(object : GetUserInfoQueryListener {
            override fun onResponse(status: UserInfoGetStatus, userInfo: UserInfo?) {
                initUserInfoData(UserInfoType.FIRST_NAME, binding.textFirstname, userInfo?.firstname)
                initUserInfoData(UserInfoType.LAST_NAME, binding.textLastname, userInfo?.lastname)
                initUserInfoData(UserInfoType.PSEUDO, binding.textPseudo, userInfo?.pseudo)
            }
        })
    }

    private fun initAutostartSection() {
        initTitle(binding.titleAutostart, R.string.parameters_auto_start_title, R.drawable.ic_autostart)
        manageAutoStartDescription(viewModel.isAutoStartEnabled(this))
        binding.switchAutostart.apply {
            isChecked = viewModel.isAutoStartEnabled(this@SettingsActivity)
            setOnClickListener {
                viewModel.activateAutoStart(this@SettingsActivity, isChecked)
                manageAutoStartDescription(isChecked)
            }
        }
    }

    private fun manageAutoStartDescription(isEnabled: Boolean) {
        binding.descriptionAutostart.apply {
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
        initTitle(binding.titleNotifications, R.string.parameters_notification_title, R.drawable.ic_notifications)
        binding.descriptionNotifications.normalText(DriveKitUI.colors.complementaryFontColor())
        binding.buttonNotifications.headLine2(DriveKitUI.colors.secondaryColor())
        binding.buttonNotifications.setOnClickListener {
            NotificationSettingsActivity.launchActivity(this)
        }
    }

    private fun initLogoutSection() {
        binding.buttonLogoutAccount.apply {
            normalText(DriveKitUI.colors.criticalColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
            setOnClickListener {
                manageLogoutClick()
            }
        }
    }

    private fun initDeleteAccountSection() {
        initTitle(binding.titleAccountDeletion, R.string.parameters_delete_account_title, R.drawable.dk_trash)
        binding.buttonDeleteAccount.apply {
            normalText(DriveKitUI.colors.criticalColor())
            setTypeface(DriveKitUI.primaryFont(context), Typeface.BOLD)
            setOnClickListener {
                manageDeleteAccount()
            }
        }
        binding.apiInfoAccountDeletion.normalText(DriveKitUI.colors.warningColor())
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
            val size = context.resources.getDimension(com.drivequant.drivekit.common.ui.R.dimen.dk_ic_big).toInt()
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
        val view = inflater.inflate(com.drivequant.drivekit.common.ui.R.layout.dk_alert_dialog_edit_value, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(view)
        val alertDialog = builder.create()
        val titleTextView = view.findViewById<TextView>(R.id.text_view_title)
        val editText = view.findViewById<TextInputEditText>(com.drivequant.drivekit.common.ui.R.id.edit_text_field)
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
                DialogInterface.BUTTON_POSITIVE, getString(com.drivequant.drivekit.common.ui.R.string.dk_common_validate)) { dialog, _ ->
                viewModel.updateUserInfo(type, editText.text.toString())
                dialog.dismiss()
            }
            setButton(
                DialogInterface.BUTTON_NEGATIVE, getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
            show()
        }
    }

    private fun manageLogoutClick() {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this)
            .layout(com.drivequant.drivekit.common.ui.R.layout.template_alert_dialog_layout)
            .positiveButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_confirm)) { _, _ ->
                viewModel.logout(this@SettingsActivity)
            }
            .negativeButton(getString(com.drivequant.drivekit.common.ui.R.string.dk_common_cancel))
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_title)
        val descriptionTextView = alertDialog.findViewById<TextView>(com.drivequant.drivekit.common.ui.R.id.text_view_alert_description)

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
