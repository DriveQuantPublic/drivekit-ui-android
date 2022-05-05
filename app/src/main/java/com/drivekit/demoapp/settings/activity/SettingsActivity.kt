package com.drivekit.demoapp.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.notification.settings.activity.NotificationSettingsActivity
import com.drivekit.demoapp.settings.viewmodel.SettingsViewModel
import com.drivekit.demoapp.splashscreen.activity.SplashScreenActivity
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.extension.headLine1
import com.drivequant.drivekit.common.ui.extension.headLine2
import com.drivequant.drivekit.common.ui.extension.normalText
import com.drivequant.drivekit.common.ui.utils.DKAlertDialog
import com.drivequant.drivekit.core.driver.GetUserInfoQueryListener
import com.drivequant.drivekit.core.driver.UserInfo
import com.drivequant.drivekit.core.driver.UserInfoGetStatus
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
            viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        }
    }

    private fun initConfiguration() {
        checkViewModelInitialization()
        initUserInfoSection()
        initAutostartSection()
        initNotificationSection()
        initLogoutSection()
        listOf(view_separator_1, view_separator_2, view_separator_3, view_separator_4, view_separator_5).forEach {
            it.setBackgroundColor(DriveKitUI.colors.neutralColor())
        }
    }

    private fun initUserInfoSection() {
        initTitle(title_account, R.string.parameters_account_title, R.drawable.ic_account)
        description_account.normalText()

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
        description_autostart.apply {
            text = getString(R.string.parameters_auto_start_enabled)
            normalText(DriveKitUI.colors.complementaryFontColor())
        }
        switch_autostart.apply {
            isChecked = viewModel.isAutoStartEnabled()
            setOnClickListener {
                viewModel.activateAutoStart(isChecked)
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
        button_delete_account.apply {
            normalText(DriveKitUI.colors.criticalColor())
            setOnClickListener {
                manageLogoutClick()
            }
        }
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
        }
    }

    private fun manageLogoutClick() {
        val alertDialog = DKAlertDialog.LayoutBuilder()
            .init(this)
            .layout(R.layout.template_alert_dialog_layout)
            .positiveButton(getString(R.string.dk_common_confirm), positiveListener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    viewModel.reset(this@SettingsActivity)
                }
            })
            .negativeButton(getString(R.string.dk_common_cancel))
            .show()

        val titleTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_title)
        val descriptionTextView = alertDialog.findViewById<TextView>(R.id.text_view_alert_description)

        titleTextView?.text = getString(R.string.app_name)
        descriptionTextView?.text = getString(R.string.logout_confirmation)

        titleTextView?.headLine1()
        descriptionTextView?.normalText()

        viewModel.logoutLiveData.observe(this@SettingsActivity) {
            val intent = Intent(this@SettingsActivity, SplashScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    enum class UserInfoType {
        FIRST_NAME, LAST_NAME, PSEUDO
    }
}