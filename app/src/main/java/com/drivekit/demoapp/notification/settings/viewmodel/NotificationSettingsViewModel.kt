package com.drivekit.demoapp.notification.settings.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel

internal class NotificationSettingsViewModel : ViewModel() {
    private val isPreOreo = Build.VERSION.SDK_INT < Build.VERSION_CODES.O

    fun isFinishedChannelEnabled(): Boolean {
        // TODO
        return true
    }

    fun isCancelledChannelEnabled(): Boolean {
        // TODO
        return true
    }
}