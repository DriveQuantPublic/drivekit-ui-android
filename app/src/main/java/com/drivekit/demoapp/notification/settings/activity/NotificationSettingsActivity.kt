package com.drivekit.demoapp.notification.settings.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.drivekit.demoapp.dashboard.viewmodel.DashboardViewModel
import com.drivekit.demoapp.notification.settings.viewmodel.NotificationSettingsViewModel
import com.drivekit.drivekitdemoapp.R
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
        title = getString(R.string.notifications_header)

        initConfiguration()

    }

    private fun initConfiguration() {
        checkViewModelInitialization()
        val finishedChannelEnabled = viewModel.isFinishedChannelEnabled()
        notification_trip_finished.setChecked(finishedChannelEnabled)
        notification_trip_finished.setOnClickListener {

        }

        val cancelledChannelEnabled = viewModel.isCancelledChannelEnabled()
        notification_trip_cancelled.setChecked(cancelledChannelEnabled)
    }

    private fun updateDescription() {
        checkViewModelInitialization()

    }

    private fun checkViewModelInitialization() {
        if (!this::viewModel.isInitialized) {
            viewModel = ViewModelProviders.of(this).get(NotificationSettingsViewModel::class.java)
        }
    }

}