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
import androidx.lifecycle.ViewModelProvider
import com.drivekit.demoapp.notification.enum.DKNotificationChannel
import com.drivekit.demoapp.notification.settings.viewmodel.NotificationSettingsViewModel
import com.drivekit.drivekitdemoapp.R
import com.drivekit.drivekitdemoapp.databinding.ActivitySettingsNotificationsBinding
import com.drivequant.drivekit.common.ui.component.SwitchSettings
import com.drivequant.drivekit.common.ui.extension.normalText

internal class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: NotificationSettingsViewModel
    private lateinit var binding: ActivitySettingsNotificationsBinding

    companion object {
        fun launchActivity(activity: Activity) {
            val intent = Intent(activity, NotificationSettingsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.root.findViewById(com.drivequant.drivekit.common.ui.R.id.dk_toolbar))
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
            viewModel = ViewModelProvider(this)[NotificationSettingsViewModel::class.java]
        }

        binding.textViewNotificationsDescription.normalText()

        binding.notificationStartTrip.apply {
            if (viewModel.isChannelEnabled(context, DKNotificationChannel.TRIP_STARTED)) {
                hideWarning()
            } else {
                setWarning()
            }
            setNotificationDescription(getString(viewModel.getTripStartedChannelDescriptionResId(context)))
            setOnClickListener {
                redirectTripStartedSettings(context)
            }
        }
        configureSwitchSettings(binding.notificationTripCancelled, DKNotificationChannel.TRIP_CANCELLED)
        configureSwitchSettings(binding.notificationTripFinished, DKNotificationChannel.TRIP_ENDED)
    }

    private fun configureSwitchSettings(
        switchSettings: SwitchSettings,
        notificationChannel: DKNotificationChannel
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
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, DKNotificationChannel.TRIP_STARTED.getChannelId())
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
