package com.drivekit.demoapp.notification.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.notification.enum.NotificationChannel
import com.drivekit.demoapp.notification.settings.viewmodel.NotificationSettingsViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivequant.drivekit.common.ui.DriveKitUI
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.extension.normalText
import kotlinx.android.synthetic.main.activity_settings_notifications.*

internal class NotificationSettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: NotificationSettingsViewModel

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, NotificationSettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_notifications)
        val toolbar = findViewById<Toolbar>(R.id.dk_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.notifications_header)
    }

    override fun onResume() {
        super.onResume()
        initConfiguration()
    }

    private fun initConfiguration() {
        val context = this@NotificationSettingsActivity
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(NotificationSettingsViewModel::class.java)
        }

        text_view_notifications_description.normalText(DriveKitUI.colors.complementaryFontColor())

        notification_start_trip.apply {
            if (viewModel.isChannelEnabled(context, NotificationChannel.TRIP_STARTED)) {
                hideWarning()
            } else {
                setWarning()
            }
            setNotificationDescription(getString(viewModel.getTripStartedChannelDescriptionResId(context)))
            setOnClickListener {
                redirectTripStartedSettings(context)
            }
        }
        configureSwitchSettings(notification_trip_cancelled, NotificationChannel.TRIP_CANCELLED)
        configureSwitchSettings(notification_trip_finished, NotificationChannel.TRIP_ENDED)

        separator_first.setBackgroundColor(DriveKitUI.colors.neutralColor())
        separator_second.setBackgroundColor(DriveKitUI.colors.neutralColor())
    }

    private fun configureSwitchSettings(
        switchSettings: SwitchSettings,
        notificationChannel: NotificationChannel
    ) {
        switchSettings.apply {
            setTitle(getString(viewModel.getChannelTitleResId(notificationChannel)))
            setDescription(getString(viewModel.getChannelDescriptionResId(notificationChannel)))
            setChecked(viewModel.isChannelEnabled(context, notificationChannel))
            setListener(object : SwitchSettings.SwitchListener {
                override fun onSwitchChanged(isChecked: Boolean) {
                    viewModel.manageChannel(context, isChecked(), notificationChannel)
                }
            })
        }
    }

    private fun redirectTripStartedSettings(context: Context) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannel.TRIP_STARTED.getChannelId())
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            else -> {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:" + context.packageName)
            }
        }
        context.startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}